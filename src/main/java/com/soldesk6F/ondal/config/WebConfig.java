package com.soldesk6F.ondal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler("/img/**")
			.addResourceLocations("file:C:\\ondal\\SoldeskTeamProject\\src\\main\\resources\\static\\img");	//이상혁
			
			
	}
	 @Override
	    public void addCorsMappings(CorsRegistry registry) {
	        // 모든 경로에 대해 CORS 허용
	        registry.addMapping("/**")
	                .allowedOrigins("http://localhost:8080", "https://localhost:8443") // 허용할 도메인
	                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
	                .allowedHeaders("Authorization", "Content-Type") // 허용할 헤더
	                .allowCredentials(true); // 쿠키를 포함한 요청을 허용
	    }
}


