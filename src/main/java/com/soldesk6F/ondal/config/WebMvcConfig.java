package com.soldesk6F.ondal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private GuestIdInterceptor guestIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(guestIdInterceptor)
                .addPathPatterns("/**") // 전체 경로에 적용
                .excludePathPatterns("/static/**", "/css/**", "/js/**", "/images/**"); // 정적 리소스 제외
    }
}
