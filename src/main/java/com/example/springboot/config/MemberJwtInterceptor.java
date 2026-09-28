package com.example.springboot.config;

import com.example.springboot.exception.BusinessException;
import com.example.springboot.mall.member.MallJwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Component
public class MemberJwtInterceptor implements HandlerInterceptor {
    private final MallJwtService jwtService;
    private final StringRedisTemplate redisTemplate;

    public MemberJwtInterceptor(MallJwtService jwtService, StringRedisTemplate redisTemplate) {
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录");
        }
        try {
            String token = authorization.substring(7);
            Claims claims = jwtService.parse(token);
            if (!"MEMBER".equals(claims.get("principalType", String.class))) {
                throw new BusinessException(401, "会员身份无效");
            }
            Long memberId = Long.valueOf(claims.getSubject());
            String current = redisTemplate.opsForValue().get("mall:login:member:" + memberId);
            if (!Objects.equals(token, current)) {
                throw new BusinessException(401, "会员登录已失效");
            }
            request.setAttribute("memberId", memberId);
            request.setAttribute("memberPhone", claims.get("username", String.class));
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
    }
}
