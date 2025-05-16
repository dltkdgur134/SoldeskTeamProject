package com.soldesk6F.ondal.chat.controller;

import com.soldesk6F.ondal.chat.dto.ChatMessageDto;
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

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final OrderService          orderService;

    /**
     * 주문 채팅 엔드포인트
     * <pre>
     * 클라이언트   → /app/chat/{orderId}  (MessageMapping)
     * 서버 브로커 → /user/queue/chat      (convertAndSendToUser)
     * </pre>
     */
    @MessageMapping("/chat/{orderId}")
    public void sendChatMessage(@DestinationVariable UUID orderId,
                                ChatMessageDto          message,
                                Authentication          authentication) {

        /* ===== 1. 보낸 사람(로그인 사용자) 확인 ===== */
        CustomUserDetails sender = (CustomUserDetails) authentication.getPrincipal();
        UUID senderUuid = sender.getUserUuid();

        log.info("💬 CHAT  order={}  from={}({}) : {}",
                 orderId, senderUuid, sender.getUsername(), message.getText());

        /* ===== 2. 주문 정보로 채팅 참여자(고객·점주·라이더) 찾기 ===== */
        Order order = orderService.findOrder(orderId);     // 예외 처리는 서비스 내부에서!

        UUID userUuid  = order.getUser().getUserUuid();
        UUID storeUuid = order.getStore().getStoreId();
        UUID riderUuid = order.getRider() != null
                         ? order.getRider().getRiderId()
                         : null;

        /* ===== 3. DTO 에 추가 정보 세팅 (선택) ===== */
        message.setSenderId(senderUuid.toString());        // 누가 보냈는지
        message.setOrderId(orderId);            // 어느 주문인지

        /* ===== 4. 각 참여자에게 전송 ===== */
        messagingTemplate.convertAndSendToUser(userUuid.toString(),  "/queue/chat", message);
        messagingTemplate.convertAndSendToUser(storeUuid.toString(), "/queue/chat", message);
        if (riderUuid != null) {
            messagingTemplate.convertAndSendToUser(riderUuid.toString(), "/queue/chat", message);
        }
    }
}
