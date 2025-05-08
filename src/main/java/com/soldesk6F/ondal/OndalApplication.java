package com.soldesk6F.ondal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication (exclude=SecurityAutoConfiguration.class)
public class OndalApplication {

	public static void main(String[] args) {
		SpringApplication.run(OndalApplication.class, args);
	}
	
}
