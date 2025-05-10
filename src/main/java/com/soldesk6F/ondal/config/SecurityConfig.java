package com.soldesk6F.ondal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import com.soldesk6F.ondal.login.CustomAuthFailureHandler;
import com.soldesk6F.ondal.login.CustomOAuth2UserService;
import com.soldesk6F.ondal.login.CustomUserDetailsService;
import com.soldesk6F.ondal.login.OAuth2LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    private final OAuth2LoginSuccessHandler OAuth2LoginSuccessHandler;
    private final CustomAuthFailureHandler customAuthFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomUserDetailsService customUserDetailsService;


    public SecurityConfig(CustomUserDetailsService customUserDetailsService ,CustomOAuth2UserService customOAuth2UserService, 
    		CustomAuthFailureHandler customAuthFailureHandler ,OAuth2LoginSuccessHandler OAuth2LoginSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthFailureHandler = customAuthFailureHandler;
        this.customOAuth2UserService = customOAuth2UserService;
        this.OAuth2LoginSuccessHandler = OAuth2LoginSuccessHandler;
        

    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(auth -> auth
//                  .requestMatchers("/", "/alert", "/login/**", "/register/**", "/css/**", "/js/**").permitAll()
////                   .requestMatchers("/admin/**").hasRole(Role.OWNER)
////                   .requestMatchers("/")
//                   .anyRequest().authenticated()
//            )
//            .formLogin(form -> form
//                .loginPage("/login/tryLogin")
//                .defaultSuccessUrl("/", true)
//                .permitAll()
//            )
//            .logout(logout -> logout      
//                .logoutSuccessUrl("/")
//            ).oauth2Login(oauth2 -> oauth2
//                    .loginPage("/login/tryLogin") // 커스텀 로그인 페이지 설정
//                    .userInfoEndpoint(userInfo -> userInfo
//                    .userService(customOAuth2UserService) // 사용자 정보 처리
//                    )
//                );
    
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http
    		.csrf(csrf -> csrf
    			.ignoringRequestMatchers("/api/**") // REST API는 CSRF 무시
    		)
    		.authorizeHttpRequests(auth -> auth
    			.requestMatchers("/**","/register","/oauth2/**", "/login/**", "/css/**", "/js/**",  "/img/**").permitAll()
    			.requestMatchers("/api/category/**").hasAuthority("OWNER")
    			.requestMatchers("/owner/**").hasAnyRole("OWNER", "ALL")
    			.anyRequest().authenticated() 
				)
			.formLogin(form -> form
				.loginPage("/login/tryLogin")           // 우리가 만든 로그인 페이지
//				.loginProcessingUrl("/login")  // 로그인 form의 action 주소 (POST)
				.defaultSuccessUrl("/",true)        // 로그인 성공 후 이동할 주소
				.failureUrl("/login?error")    // 로그인 실패 시 주소
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
				.permitAll()
			)
			.oauth2Login(oauth2 -> oauth2
				.loginPage("/login/tryOAuthLogin")
				.userInfoEndpoint(userInfo -> userInfo
					.userService(customOAuth2UserService)
				)
				.successHandler(OAuth2LoginSuccessHandler)
			)
			.exceptionHandling(exception -> exception
				.accessDeniedHandler((request, response, accessDeniedException) ->
					response.sendRedirect("/access-denied")
				)
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