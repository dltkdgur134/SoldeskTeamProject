package com.soldesk6F.ondal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler("/img/**")
//			.addResourceLocations("file:C:\\TeamProject\\SoldeskTeamProject\\src\\main\\resources\\static\\img");
//			.addResourceLocations("file:C:\\Users\\sdedu\\Desktop\\Project_Ondal\\git_SoldeskTeamProject\\src\\main\\resources\\static\\img");
			
			// 나은석
			.addResourceLocations("file:C:\\Users\\sdedu\\Desktop\\Project\\src\\main\\resources\\static\\img");
			
	}
}


