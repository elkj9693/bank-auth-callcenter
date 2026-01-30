package com.gwangjin.issuerwas.config;

import com.gwangjin.issuerwas.common.security.ServiceTokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ServiceTokenInterceptor serviceTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serviceTokenInterceptor)
                .addPathPatterns("/issuer/**");
    }

    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:5173", "http://localhost:5174", "http://localhost:5175",
                        "http://127.0.0.1:5173", "http://127.0.0.1:5174", "http://127.0.0.1:5175",
                        "http://localhost:3001", "http://127.0.0.1:3001")
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}
