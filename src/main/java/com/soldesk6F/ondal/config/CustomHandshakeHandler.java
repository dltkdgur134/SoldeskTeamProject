package com.soldesk6F.ondal.config;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.soldesk6F.ondal.login.CustomUserDetails;

/**
 * WebSocket 핸드셰이크 시 세션 속성에 담아 둔 CustomUserDetails를
 * 그대로 Principal로 돌려준다.
 */
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest  request,
                                      WebSocketHandler   wsHandler,
                                      Map<String, Object> attributes) {

        /* AuthHandshakeInterceptor 에서 저장해 둔 객체 꺼내기 */
        Object principal = attributes.get("userDetails");
        if (principal instanceof CustomUserDetails cud) {
            return cud;          // ✅ 그대로 Principal(ID: getName()) 로 사용
        }

        /* 예외-케이스: 세션 정보가 없으면 임시 익명 Principal */
        return () -> "anonymous-" + System.nanoTime();
    }
}
