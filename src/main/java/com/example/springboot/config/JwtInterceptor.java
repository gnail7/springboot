package com.example.springboot.config;

import com.example.springboot.entity.Role;
import com.example.springboot.exception.BusinessException;
import com.example.springboot.mapper.RoleMapper;
import com.example.springboot.utils.JwtUtil;
import com.example.springboot.utils.RedisKeys;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JWT 登录拦截器
 *
 * <p>1. 校验请求头 Authorization: Bearer xxx；
 * 2. 结合 Redis 实现“同一账号只能一个地方登录”（key = login:token:{userId}）；
 * 3. 把当前用户的权限码写入 SecurityContext，供 @PreAuthorize 方法级鉴权使用。</p>
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Log log = LogFactory.getLog(JwtInterceptor.class);

    /** 超级管理员角色标识（通配所有权限） */
    private static final String ROLE_KEY_ADMIN = "admin";
    private static final String ALL_PERMISSION = "*:*:*";

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final RoleMapper roleMapper;

    public JwtInterceptor(JwtUtil jwtUtil, StringRedisTemplate redisTemplate, RoleMapper roleMapper) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.roleMapper = roleMapper;
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

        // 供 Controller @RequestAttribute 使用
        request.setAttribute("userId", userId);
        request.setAttribute("username", claims.get("username", String.class));

        // 供 @PreAuthorize 方法级鉴权使用
        setSecurityContext(userId);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        // 防止上下文泄漏到线程池中的下一次请求
        SecurityContextHolder.clearContext();
    }

    /** 判断 Redis 中该用户最新的 token 是否与本次一致（Redis 不可用时降级放行） */
    private boolean isCurrentToken(Long userId, String token) {
        try {
            String current = redisTemplate.opsForValue().get(RedisKeys.loginToken(userId));
            return token.equals(current);
        } catch (Exception e) {
            log.warn("Redis 不可用，本次跳过单点登录校验: " + e.getMessage());
            return true;
        }
    }

    /**
     * 把当前用户权限写入 SecurityContext。
     * authorities = 该用户全部菜单权限码（sys_menu.perms）；超级管理员额外加 *:*:*
     */
    private void setSecurityContext(Long userId) {
        Set<String> authorities = new HashSet<>();
        try {
            List<Role> roles = roleMapper.selectRolesByUserId(userId);
            List<String> perms = roleMapper.selectPermsByUserId(userId);

            if (perms != null) {
                authorities.addAll(perms);
            }
            boolean isAdmin = roles != null && roles.stream()
                    .anyMatch(role -> ROLE_KEY_ADMIN.equals(role.getRoleKey()));
            if (isAdmin) {
                authorities.add(ALL_PERMISSION);
            }
        } catch (Exception e) {
            // 权限查询失败不阻断登录态，但所有 @PreAuthorize 会因无权限而拒绝
            log.warn("加载用户权限失败: userId=" + userId + ", error=" + e.getMessage());
        }

        List<SimpleGrantedAuthority> granted =
                authorities.stream().map(SimpleGrantedAuthority::new).toList();

        // principal 用 userId，方便以后用 @AuthenticationPrincipal 取出
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, granted);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
