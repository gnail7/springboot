package com.example.springboot.mall.lab;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class FlashSaleService {
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT = new DefaultRedisScript<>(
            "local stock = tonumber(redis.call('get', KEYS[1]) or '-1') "
                    + "if stock < tonumber(ARGV[2]) then return 0 end "
                    + "if redis.call('sismember', KEYS[2], ARGV[1]) == 1 then return -1 end "
                    + "redis.call('decrby', KEYS[1], ARGV[2]) "
                    + "redis.call('sadd', KEYS[2], ARGV[1]) "
                    + "return 1", Long.class);
    private final StringRedisTemplate redisTemplate;

    public FlashSaleService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void preheat(Long activityId, int stock) {
        redisTemplate.opsForValue().set("mall:flash:stock:" + activityId, String.valueOf(stock));
        redisTemplate.delete("mall:flash:buyers:" + activityId);
    }

    public FlashSaleResult tryOrder(Long activityId, Long memberId, int quantity) {
        String orderNo = "FS" + UUID.randomUUID().toString().replace("-", "");
        Long result = redisTemplate.execute(SECKILL_SCRIPT,
                java.util.List.of("mall:flash:stock:" + activityId, "mall:flash:buyers:" + activityId),
                memberId.toString(), String.valueOf(quantity));
        if (Long.valueOf(1L).equals(result)) {
            return new FlashSaleResult(true, orderNo, "QUEUED");
        }
        if (Long.valueOf(-1L).equals(result)) {
            return new FlashSaleResult(false, null, "DUPLICATE_MEMBER");
        }
        return new FlashSaleResult(false, null, "SOLD_OUT");
    }

    public record FlashSaleResult(boolean success, String orderNo, String status) {
    }
}
