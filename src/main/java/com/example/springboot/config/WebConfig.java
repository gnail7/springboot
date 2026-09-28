package com.example.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final ApiLogInterceptor apiLogInterceptor;
    private final JwtInterceptor jwtInterceptor;
    private final MemberJwtInterceptor memberJwtInterceptor;

    public WebConfig(ApiLogInterceptor apiLogInterceptor, JwtInterceptor jwtInterceptor,
                     MemberJwtInterceptor memberJwtInterceptor) {
        this.apiLogInterceptor = apiLogInterceptor;
        this.jwtInterceptor = jwtInterceptor;
        this.memberJwtInterceptor = memberJwtInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOriginPatterns("*").allowedMethods("*")
                .allowedHeaders("*").allowCredentials(true).maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiLogInterceptor).addPathPatterns("/**");
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/register",
                        "/api/blog/public/**", "/api/mall/public/**", "/api/mall/member/**",
                        "/api/mall/lab/**");
        registry.addInterceptor(memberJwtInterceptor).addPathPatterns("/api/mall/member/**")
                .excludePathPatterns("/api/mall/member/auth/**");
    }
}
