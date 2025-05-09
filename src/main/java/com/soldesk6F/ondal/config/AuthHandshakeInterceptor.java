package com.soldesk6F.ondal.config;

import org.springframework.web.socket.server.HandshakeInterceptor;

import com.soldesk6F.ondal.login.CustomUserDetails;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;
import java.util.UUID;

public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpSession session = servletRequest.getServletRequest().getSession(false);

            if (session != null) {
            	Object contextObj = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                if (contextObj instanceof SecurityContext securityContext) {
                    Authentication authentication = securityContext.getAuthentication();

                if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
                    // userDetails에서 UUID 꺼내는 방식 (예: UserEntity에 getUserUuid() 있음)
                    UUID userUuid = UUID.fromString(((CustomUserDetails) userDetails).getName());
                    String username = userDetails.getUsername();

                    attributes.put("userUuid", userUuid);
                    attributes.put("username", username);
                }
            }
        }
        }
        return true;
    }

    @Override
    public void afterHandshake(
            org.springframework.http.server.ServerHttpRequest request,
            org.springframework.http.server.ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // 필요 시 후처리
    }
}
