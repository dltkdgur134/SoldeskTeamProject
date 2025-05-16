package com.soldesk6F.ondal.config;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.soldesk6F.ondal.login.CustomUserDetails;

public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    /** 세션에 저장된 Spring Security Context 키 */
    private static final String SEC_CTX_KEY =
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

    @Override
    public boolean beforeHandshake(ServerHttpRequest  request,
                                   ServerHttpResponse response,
                                   WebSocketHandler   wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return true;                       // SockJS XHR 등
        }

        HttpSession session = servletRequest.getServletRequest().getSession(false);
        if (session == null) {                 // 비로그인 요청도 허용
            return true;
        }

        Object ctxObj = session.getAttribute(SEC_CTX_KEY);
        if (ctxObj instanceof SecurityContext secCtx) {

            Authentication auth = secCtx.getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails cud) {

                /* === WebSocket 세션 attribute 에 사용자 정보 저장 === */
                UUID   userUuid = cud.getUserUuid();       // 커스텀 getter
                String username = cud.getUsername();

                attributes.put("userUuid", userUuid);
                attributes.put("username", username);
                attributes.put("userDetails", cud);

                // 필요하다면 권한/역할도
                // attributes.put("roleName", cud.getUser().getUserRole().name());
            }
        }
        return true;      // 핸드셰이크 계속 진행
    }

    @Override
    public void afterHandshake(ServerHttpRequest  request,
                               ServerHttpResponse response,
                               WebSocketHandler   wsHandler,
                               Exception          exception) {
        /* 후처리 필요 없으면 비워둠 */
    }
}
