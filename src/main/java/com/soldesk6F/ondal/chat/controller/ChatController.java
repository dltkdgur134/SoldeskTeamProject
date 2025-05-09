package com.soldesk6F.ondal.chat.controller;

import com.soldesk6F.ondal.chat.dto.ChatMessageDto;
import com.soldesk6F.ondal.config.CustomPrincipal;
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.owner.order.OrderService;
import com.soldesk6F.ondal.useract.order.entity.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final OrderService orderService;

    @MessageMapping("/chat/{orderId}")
    public void sendChatMessage(@DestinationVariable String orderIdStr,
                                ChatMessageDto chatMessage,
                                Principal principal) {
        log.info("🔎 Incoming chat for orderIdStr: {}", orderIdStr);

        if (principal instanceof CustomPrincipal userPrincipal) {
            UUID uuid = userPrincipal.getUserUuid();
            log.info("👤 WebSocket 유저 UUID: {}", uuid);
        }

        Authentication auth = (Authentication) principal;
        Object principalObj = auth.getPrincipal();

        log.info("👤 Principal class: {}", principalObj.getClass().getName());
        log.info("👤 Principal.toString(): {}", principalObj.toString());

        if (principalObj instanceof CustomUserDetails cud) {
            log.info("✅ CustomUserDetails.getUserId(): {}", cud.getUserId());
            log.info("✅ CustomUserDetails.getUser().getUserUuid(): {}", cud.getUser().getUserUuid());

            // 필요 시 Owner, Rider 등도 확인
        } else {
            log.warn("❌ Principal is not CustomUserDetails: {}", principalObj.getClass());
        }

        // UUID 파싱 방어
        UUID orderId;
        try {
            orderId = UUID.fromString(orderIdStr);
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid UUID received for orderId: {}", orderIdStr, e);
            return;
        }
        // 🧩 주문 정보로 참여자 조회
        Order order = orderService.findOrder(orderId);
        UUID userId = order.getUser().getUserUuid();
        UUID storeId = order.getStore().getStoreId();
        UUID riderId = order.getRider() != null ? order.getRider().getRiderId() : null;

        // 📨 메시지 전송 (각 참여자에게)
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/chat", chatMessage);
        messagingTemplate.convertAndSendToUser(storeId.toString(), "/queue/chat", chatMessage);
        if (riderId != null) {
            messagingTemplate.convertAndSendToUser(riderId.toString(), "/queue/chat", chatMessage);
        }

        // ✅ 로그
        log.debug("Chat sent to order {} from {} ({}): {}", orderId,
                chatMessage.getSenderId(), chatMessage.getSenderType(), chatMessage.getText());

        // ✅ 저장 처리 (선택 구현 가능)
        // chatService.save(chatMessage);
    }
}