//package com.soldesk6F.ondal;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/register", "/css/**", "/js/**").permitAll() // 회원가입 페이지는 허용
//                .anyRequest().authenticated() // 나머지는 로그인 필요
//            )
//            .formLogin(Customizer.withDefaults()) // 기본 로그인 페이지 사용
//            .csrf(csrf -> csrf.disable()); // CSRF 끄기 (테스트용)
//
//        return http.build();
//    }
//    
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
//
