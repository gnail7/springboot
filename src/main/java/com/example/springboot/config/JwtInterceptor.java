package com.example.springboot.config;

import com.example.springboot.exception.BusinessException;
import com.example.springboot.utils.JwtUtil;
import com.example.springboot.utils.RedisKeys;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 登录拦截器
 *
 * <p>校验请求头 Authorization: Bearer xxx，并结合 Redis 实现
 * “同一账号同时只能一个地方登录”：登录时把最新 token 写入 Redis
 * （key = login:token:{userId}），新登录会覆盖旧 token；
 * 旧 token 再来访问时与 Redis 中的不一致 → 401，实现“顶号”。</p>
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Log log = LogFactory.getLog(JwtInterceptor.class);

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    public JwtInterceptor(JwtUtil jwtUtil, StringRedisTemplate redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 只拦截 Controller 方法（放过静态资源等）
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录");
        }
        String token = authorization.substring(7);

        Claims claims;
        try {
            claims = jwtUtil.parseToken(token);
        } catch (Exception e) {
            // token 过期 / 被篡改 / 格式错误，统一按 401 处理
            throw new BusinessException(401, "登录已过期，请重新登录");
        }

        Long userId = Long.valueOf(claims.getSubject());

        // 单点登录校验：Redis 中该用户最新的 token 必须与本次一致
        if (!isCurrentToken(userId, token)) {
            throw new BusinessException(401, "账号已在其他设备登录，请重新登录");
        }

        request.setAttribute("userId", userId);
        request.setAttribute("username", claims.get("username", String.class));
        return true;
    }

    /**
     * 判断 Redis 中该用户最新的 token 是否与本次一致。
     * Redis 不可用时降级放行（开发期方便）；生产环境可改为返回 false 强制 401。
     */
    private boolean isCurrentToken(Long userId, String token) {
        try {
            String current = redisTemplate.opsForValue().get(RedisKeys.loginToken(userId));
            return token.equals(current);
        } catch (Exception e) {
            log.warn("Redis 不可用，本次跳过单点登录校验: " + e.getMessage());
            return true;
        }
    }
}
