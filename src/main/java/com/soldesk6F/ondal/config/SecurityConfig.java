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
    			.requestMatchers("/**","/register", "/login/**", "/css/**", "/js/**",  "/img/**").permitAll() 
    			.anyRequest().authenticated() 
				)
			.formLogin(form -> form
				.loginPage("/login/tryLogin")           // 우리가 만든 로그인 페이지
				.loginProcessingUrl("/login")  // 로그인 form의 action 주소 (POST)
				.defaultSuccessUrl("/",true)        // 로그인 성공 후 이동할 주소
				.failureUrl("/login?error")    // 로그인 실패 시 주소
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
				.permitAll()
			)
			.csrf(csrf -> csrf.disable());

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//.authorizeHttpRequests(auth -> auth // 임시 로그인 비활성화
		//.anyRequest().permitAll() // 모든 요청 허용
		//)
		//.formLogin(form -> form.disable()) // 기본 로그인 페이지 비활성화
		//.logout(logout -> logout.disable()) // 로그아웃도 비활성화
		//.csrf(csrf -> csrf.disable()); // CSRF 비활성화
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        
        return http.build();
    }
    
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        // 실제 서비스에서는 절대 사용 금지. 테스트 용도로만!
//        return NoOpPasswordEncoder.getInstance();
//    }
    
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
    
}