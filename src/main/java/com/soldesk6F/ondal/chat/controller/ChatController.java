package com.soldesk6F.ondal.chat.controller;


import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soldesk6F.ondal.chat.dto.ChatMessageDto;
import com.soldesk6F.ondal.chat.dto.ChatResponseDto;
import com.soldesk6F.ondal.chat.entity.ChatMessage;
import com.soldesk6F.ondal.chat.entity.ChatRoom;
import com.soldesk6F.ondal.chat.service.ChatService;
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.login.OAuth2LoginSuccessHandler;
import com.soldesk6F.ondal.owner.order.OrderService;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

@Controller
public class ChatController {
	
    private final ObjectMapper objectMapper;

    private final OAuth2LoginSuccessHandler OAuth2LoginSuccessHandler;

    private final SimpMessagingTemplate messagingTemplate;
    
    private final OrderService orderService;
    
    private final ChatService chatService;
    
    
    public ChatController(SimpMessagingTemplate messagingTemplate, OAuth2LoginSuccessHandler OAuth2LoginSuccessHandler, ObjectMapper objectMapper, OrderService orderService, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.OAuth2LoginSuccessHandler = OAuth2LoginSuccessHandler;
        this.objectMapper = objectMapper;
		this.orderService = orderService;
		this.chatService = chatService;
    }

    /**
     * 클라이언트가 "/app/chat/{orderId}" 로 보낸 메시지를
     * "/topic/chat/{orderId}" 로 다시 브로드캐스트
     */
    @MessageMapping("/chat/{orderId}")
    public void sendChatMessage(@DestinationVariable("orderId") String orderId,
                                ChatMessageDto          message,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        /* ===== 1. 보낸 사람(로그인 사용자) 확인 ===== */
        //CustomUserDetails sender = (CustomUserDetails) authentication.getPrincipal();
    	//UUID senderUuid = sender.getUserUuid();
    	//CustomUserDetails senderUserDetails = (CustomUserDetails) authentication.getPrincipal();
    	//User sender = senderUserDetails.getUser();
    	User sender = customUserDetails.getUser();
    	String senderId = sender.getUserUuidAsString();
    	UUID senderUuid = UUID.fromString(senderId);
    	
//        log.info("💬 CHAT  order={}  from={}({}) : {}",
//                 orderId, senderUuid, sender.getUsername(), message.getText());

        /* ===== 2. 주문 정보로 채팅 참여자(고객·점주·라이더) 찾기 ===== */
        UUID orderUuid = UUID.fromString(orderId);
        Order order = orderService.findOrder(orderUuid);     // 예외 처리는 서비스 내부에서!

        UUID userUuid  = order.getUser().getUserUuid();
        UUID storeUuid = order.getStore().getStoreId();
        UUID riderUuid = order.getRider() != null
                         ? order.getRider().getRiderId()
                         : null;

        
        /* ===== 2-1. 메시지 저장 ===== */
        ChatRoom chatRoom = chatService.saveChatroom(order);
        
        chatService.saveMessage(chatRoom, sender, message);
        
        
        /* ===== 3. DTO 에 추가 정보 세팅 (선택) ===== */
        message.setSenderId(senderUuid.toString());        // 누가 보냈는지
        message.setOrderId(orderUuid);            // 어느 주문인지
        message.setSenderName(sender.getUserId());
        

        /* ===== 4. 각 참여자에게 전송 ===== */
        messagingTemplate.convertAndSendToUser(userUuid.toString(),  "/queue/chat", message);
        messagingTemplate.convertAndSendToUser(storeUuid.toString(), "/queue/chat", message);
        if (riderUuid != null) {
            messagingTemplate.convertAndSendToUser(riderUuid.toString(), "/queue/chat", message);
        }
        messagingTemplate.convertAndSend("/topic/chat/" + orderId, message);
        
    }
    
    @GetMapping("/chat/getPrevMsgs/{orderId}")
    public ResponseEntity<List<ChatResponseDto>> getPreviousMessages( @PathVariable("orderId") String orderId,
            @AuthenticationPrincipal(expression="user.userId") String userId) {
    	List<ChatResponseDto> chatMessages = chatService.getChatMessage(orderId);
    	return ResponseEntity.ok(chatMessages);
    }
    
}