package com.example.springboot.mall.message;

import com.example.springboot.mapper.MallOutboxEventMapper;
import com.example.springboot.mall.common.lock.RedisLockService;
import com.example.springboot.mall.message.entity.MallOutboxEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OutboxPublisherJob {
    private final MallOutboxEventMapper outboxMapper;
    private final RabbitTemplate rabbitTemplate;
    private final RedisLockService lockService;
    private final int batchSize;

    public OutboxPublisherJob(MallOutboxEventMapper outboxMapper, RabbitTemplate rabbitTemplate,
                              RedisLockService lockService,
                              @Value("${mall.message.outbox-batch-size:100}") int batchSize) {
        this.outboxMapper = outboxMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.lockService = lockService;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${mall.job.fixed-delay-ms:30000}")
    public void publishPending() {
        String key = "mall:lock:job:publishOutboxEvents";
        String token = lockService.newToken();
        if (!lockService.tryLock(key, token, Duration.ofSeconds(25))) {
            return;
        }
        try {
            List<MallOutboxEvent> events = outboxMapper.findPending(LocalDateTime.now(), batchSize);
            for (MallOutboxEvent event : events) {
                try {
                    rabbitTemplate.invoke(operations -> {
                        operations.convertAndSend(MallRabbitConfig.EXCHANGE, event.getEventType(), event.getPayloadJson(), message -> {
                            message.getMessageProperties().setMessageId(event.getEventId());
                            return message;
                        });
                        operations.waitForConfirmsOrDie(5000);
                        return null;
                    });
                    outboxMapper.markPublished(event.getId());
                } catch (Exception e) {
                    outboxMapper.markRetry(event.getId(), LocalDateTime.now().plusSeconds(30), e.getMessage());
                }
            }
        } finally {
            lockService.unlock(key, token);
        }
    }
}
