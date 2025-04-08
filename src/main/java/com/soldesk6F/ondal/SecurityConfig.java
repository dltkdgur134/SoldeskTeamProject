package com.soldesk6F.ondal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http
//    		.authorizeHttpRequests(auth -> auth
//    			.requestMatchers("/register", "/login", "/css/**", "/js/**",  "/img/**").permitAll() 
//    			.anyRequest().authenticated() 
//				)
//			.formLogin(form -> form
//				.loginPage("/login")           // 우리가 만든 로그인 페이지
//				.loginProcessingUrl("/login")  // 로그인 form의 action 주소 (POST)
//				.defaultSuccessUrl("/")        // 로그인 성공 후 이동할 주소
//				.failureUrl("/login?error")    // 로그인 실패 시 주소
//				.permitAll()
//			)
//			.logout(logout -> logout
//				.logoutUrl("/logout")
//				.logoutSuccessUrl("/login?logout")
//				.permitAll()
//			)
//			.csrf(csrf -> csrf.disable());
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        .authorizeHttpRequests(auth -> auth // 임시 로그인 비활성화
                .anyRequest().permitAll() // 모든 요청 허용
            )
            .formLogin(form -> form.disable()) // 기본 로그인 페이지 비활성화
            .logout(logout -> logout.disable()) // 로그아웃도 비활성화
            .csrf(csrf -> csrf.disable()); // CSRF 비활성화
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    	return http.build();
	}
    
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

