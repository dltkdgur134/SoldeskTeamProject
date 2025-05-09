package com.soldesk6F.ondal.config;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.soldesk6F.ondal.login.CustomUserDetails;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
    	UUID userUuid = (UUID) attributes.get("userUuid");
        String username = (String) attributes.get("username");

        return new CustomPrincipal(userUuid, username);
    }
}
