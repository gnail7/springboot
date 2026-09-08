package com.example.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 *
 * <p>登录态校验仍由自研 JwtInterceptor 完成（token + Redis 单点登录），
 * 因此这里：关闭 CSRF / 无状态 Session / 所有请求放行，不启用任何登录页与过滤器。</p>
 *
 * <p>方法级鉴权通过 {@link EnableMethodSecurity} 开启，
 * Controller 上写 {@code @PreAuthorize("@permissionService.hasPerm('system:user:add')")}，
 * 权限来自 JwtInterceptor 解析 token 后写入 SecurityContext 的 authorities。</p>
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 接口都是无状态 JWT，不需要 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 由 MVC 层统一处理 CORS
                .cors(Customizer.withDefaults())
                // 无状态：不创建 Session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 登录与否由我们的拦截器决定，这里全部放行
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
