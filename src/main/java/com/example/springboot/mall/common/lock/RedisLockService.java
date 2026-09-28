package com.example.springboot.mall.common.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Service
public class RedisLockService {
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end", Long.class);
    private static final DefaultRedisScript<Long> RENEW_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('pexpire', KEYS[1], ARGV[2]) else return 0 end", Long.class);
    private final StringRedisTemplate redisTemplate;

    public RedisLockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String newToken() {
        return UUID.randomUUID().toString();
    }

    public boolean tryLock(String key, String token, Duration ttl) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, token, ttl);
        return Boolean.TRUE.equals(success);
    }

    public boolean unlock(String key, String token) {
        Long result = redisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(key), token);
        return Long.valueOf(1L).equals(result);
    }

    public boolean renew(String key, String token, Duration ttl) {
        Long result = redisTemplate.execute(RENEW_SCRIPT, Collections.singletonList(key), token,
                String.valueOf(ttl.toMillis()));
        return Long.valueOf(1L).equals(result);
    }
}
