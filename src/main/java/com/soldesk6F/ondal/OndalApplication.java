package com.soldesk6F.ondal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication (exclude=SecurityAutoConfiguration.class)
public class OndalApplication {

	public static void main(String[] args) {
		SpringApplication.run(OndalApplication.class, args);
	}
	
}
