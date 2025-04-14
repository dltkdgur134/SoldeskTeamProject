/**
 * 제작자 : 곽준영
 * 클래스 설명 : 로그인 실패 원인 로그로 출력
 */
package com.soldesk6F.ondal.user;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.naming.AuthenticationException;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {


	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			org.springframework.security.core.AuthenticationException exception) throws IOException, ServletException {
        // 로그인 실패 원인 로그로 출력
        System.out.println("로그인 실패 이유: " + exception.getMessage());

        // 실패 원인에 따라 리다이렉트할 수도 있음
        response.sendRedirect("/alert?error=" + URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8));		
	}
}