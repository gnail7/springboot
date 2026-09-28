package com.example.springboot.mall.message;

import com.example.springboot.mapper.MallMessageConsumeRecordMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class OrderCreatedConsumer {
    private static final String GROUP = "mall-order-created-demo-consumer";
    private final MallMessageConsumeRecordMapper consumeRecordMapper;

    public OrderCreatedConsumer(MallMessageConsumeRecordMapper consumeRecordMapper) {
        this.consumeRecordMapper = consumeRecordMapper;
    }

    @RabbitListener(queues = MallRabbitConfig.ORDER_CREATED_QUEUE, ackMode = "MANUAL")
    public void consume(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            String eventId = message.getMessageProperties().getMessageId();
            if (eventId == null || eventId.isBlank()) {
                eventId = Integer.toHexString(body.hashCode());
            }
            consumeRecordMapper.insertIgnore(GROUP, eventId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
