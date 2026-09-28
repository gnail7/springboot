package com.example.springboot.mall.job;

import com.example.springboot.mapper.MallInventoryMapper;
import com.example.springboot.mapper.MallJobExecutionMapper;
import com.example.springboot.mapper.MallOrderItemMapper;
import com.example.springboot.mapper.MallOrderMapper;
import com.example.springboot.mall.common.lock.RedisLockService;
import com.example.springboot.mall.entity.MallInventory;
import com.example.springboot.mall.entity.MallOrder;
import com.example.springboot.mall.entity.MallOrderItem;
import com.example.springboot.mall.job.entity.MallJobExecution;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class MallOrderTimeoutJob {
    private static final String JOB_NAME = "closeExpiredOrders";
    private final MallOrderMapper orderMapper;
    private final MallOrderItemMapper itemMapper;
    private final MallInventoryMapper inventoryMapper;
    private final MallJobExecutionMapper executionMapper;
    private final RedisLockService lockService;

    public MallOrderTimeoutJob(MallOrderMapper orderMapper, MallOrderItemMapper itemMapper,
                               MallInventoryMapper inventoryMapper, MallJobExecutionMapper executionMapper,
                               RedisLockService lockService) {
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.inventoryMapper = inventoryMapper;
        this.executionMapper = executionMapper;
        this.lockService = lockService;
    }

    @Scheduled(fixedDelayString = "${mall.job.fixed-delay-ms:30000}")
    public void closeExpiredOrders() {
        String key = "mall:lock:job:" + JOB_NAME;
        String token = lockService.newToken();
        if (!lockService.tryLock(key, token, Duration.ofSeconds(25))) {
            return;
        }
        LocalDateTime started = LocalDateTime.now();
        MallJobExecution execution = new MallJobExecution();
        execution.setJobName(JOB_NAME);
        execution.setExecutionId(UUID.randomUUID().toString());
        execution.setExecutionStatus("RUNNING");
        execution.setStartedAt(started);
        execution.setProcessedCount(0);
        executionMapper.insert(execution);
        int processed = 0;
        try {
            List<MallOrder> orders = orderMapper.findExpired(LocalDateTime.now(), 100);
            for (MallOrder order : orders) {
                if (closeOne(order)) {
                    processed++;
                }
            }
            execution.setExecutionStatus("SUCCESS");
            execution.setProcessedCount(processed);
        } catch (Exception e) {
            execution.setExecutionStatus("FAILED");
            execution.setErrorMessage(e.getMessage());
            execution.setProcessedCount(processed);
        } finally {
            execution.setFinishedAt(LocalDateTime.now());
            execution.setDurationMs(Duration.between(started, execution.getFinishedAt()).toMillis());
            executionMapper.updateById(execution);
            lockService.unlock(key, token);
        }
    }

    @Transactional
    protected boolean closeOne(MallOrder order) {
        if (orderMapper.changeStatus(order.getId(), "PENDING_PAYMENT", "CANCELLED", order.getVersion()) != 1) {
            return false;
        }
        for (MallOrderItem item : itemMapper.findByOrderId(order.getId())) {
            MallInventory inventory = inventoryMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MallInventory>()
                            .eq(MallInventory::getSkuId, item.getSkuId()));
            if (inventory != null) {
                inventoryMapper.release(item.getSkuId(), item.getQuantity(), inventory.getVersion());
            }
        }
        return true;
    }
}
