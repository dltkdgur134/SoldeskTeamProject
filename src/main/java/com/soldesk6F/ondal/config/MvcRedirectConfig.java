package com.soldesk6F.ondal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcRedirectConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry reg) {
        reg.addRedirectViewController("/login", "/login/login");
    }
}
