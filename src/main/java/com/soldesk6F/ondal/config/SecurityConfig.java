package com.soldesk6F.ondal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.soldesk6F.ondal.user.CostomUserDetailsService;
import com.soldesk6F.ondal.user.CustomAuthFailureHandler;
import com.soldesk6F.ondal.user.CustomOAuth2UserService;
import com.soldesk6F.ondal.user.Role;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthFailureHandler customAuthFailureHandler;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CostomUserDetailsService costomUserDetailsService;

    public SecurityConfig(CostomUserDetailsService customUserDetailsService ,CustomOAuth2UserService customOAuth2UserService, 
    		CustomAuthFailureHandler customAuthFailureHandler) {
        this.costomUserDetailsService = customUserDetailsService;
        this.customAuthFailureHandler = customAuthFailureHandler;
        this.customOAuth2UserService = customOAuth2UserService;
        

    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(costomUserDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers("/**", "/alert", "/login/**", "/register/**", "/css/**", "/js/**").permitAll()
//            	    .requestMatchers("/admin/**").hasRole(Role.OWNER)
//            	    .requestMatchers("/")
            	    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login/tryLogin")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout		
                .logoutSuccessUrl("/")
            ).oauth2Login(oauth2 -> oauth2
                    .loginPage("/login/tryOAuthLogin") // 커스텀 로그인 페이지 설정
                    .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService) // 사용자 정보 처리
                    )
                );

        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 실제 서비스에서는 절대 사용 금지. 테스트 용도로만!
        return NoOpPasswordEncoder.getInstance();
    }
    
}