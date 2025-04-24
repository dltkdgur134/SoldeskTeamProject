package com.soldesk6F.ondal.chat.controller;


import com.soldesk6F.ondal.chat.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 클라이언트가 "/app/chat/{orderId}" 로 보낸 메시지를
     * "/topic/chat/{orderId}" 로 다시 브로드캐스트
     */
    @MessageMapping("/chat/{orderId}")
    public void relayChat(@DestinationVariable String orderId, ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/chat/" + orderId, message);
    }
}
