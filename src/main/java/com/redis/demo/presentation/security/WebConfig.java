package com.redis.demo.presentation.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SessionAuthInterceptor sessionAuthInterceptor;

    public WebConfig(SessionAuthInterceptor sessionAuthInterceptor) {
        this.sessionAuthInterceptor = sessionAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/auth/**", // auth endpoints are public
                        "/health",       // health check is public
                        "/error",        // Spring error path
                        "/favicon.ico",
                        "/css/**", "/js/**", "/images/**",
                        // Swagger/OpenAPI docs
                        "/v3/api-docs/**", 
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                );
    }
}
