package com.soldesk6F.ondal.chat.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.soldesk6F.ondal.chat.dto.ChatMessage;
import com.soldesk6F.ondal.login.OAuth2LoginSuccessHandler;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final ObjectMapper objectMapper;

    private final OAuth2LoginSuccessHandler OAuth2LoginSuccessHandler;

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate, OAuth2LoginSuccessHandler OAuth2LoginSuccessHandler, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.OAuth2LoginSuccessHandler = OAuth2LoginSuccessHandler;
        this.objectMapper = objectMapper;
    }

    /**
     * 클라이언트가 "/app/chat/{orderId}" 로 보낸 메시지를
     * "/topic/chat/{orderId}" 로 다시 브로드캐스트
     */
    @MessageMapping("/chat/{orderId}")
    public void relayChat(@DestinationVariable("orderId") String orderId, @Payload ChatMessage message ,Principal principal) {
    	System.out.println("접근자체는 성공");
    	System.out.println("✅ 메시지 보낸 사용자: " + (principal != null ? principal.getName() : "null"));
    	System.out.println("✅ 메시지 내용: " + message.getText());
        messagingTemplate.convertAndSend("/topic/chat/" + orderId, message);
    }
    
    
}