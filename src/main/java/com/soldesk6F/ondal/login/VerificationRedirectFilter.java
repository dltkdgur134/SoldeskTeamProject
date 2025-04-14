package com.soldesk6F.ondal.login;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class VerificationRedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean isVerified = true; // TO DO DB에서 VERIFIED가져와서 넣기
            String uri = request.getRequestURI();

            // 인증 안 됐고, /verify 페이지가 아니면
            if (!isVerified && !uri.startsWith("/verify")) {	
                response.sendRedirect("/verify"); // 강제 리다이렉트
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}