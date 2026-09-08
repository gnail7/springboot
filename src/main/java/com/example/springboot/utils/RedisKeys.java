package com.example.springboot.utils;

/**
 * Redis 键常量，统一管理，避免散落魔法字符串
 */
public final class RedisKeys {

    private RedisKeys() {
    }

    /**
     * 单点登录键：key = login:token:{userId}，value = 该用户当前有效的 JWT。
     * 登录时写入（覆盖即互踢），TTL = token 有效期；退出登录时删除。
     */
    public static String loginToken(Long userId) {
        return "login:token:" + userId;
    }
}
