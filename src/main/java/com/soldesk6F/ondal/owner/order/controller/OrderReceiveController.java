package com.soldesk6F.ondal.owner.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soldesk6F.ondal.owner.order.OrderService;
import com.soldesk6F.ondal.useract.order.dto.OrderRequestDto;
import com.soldesk6F.ondal.useract.order.entity.Order;


@RestController
@RequiredArgsConstructor
public class OrderReceiveController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final OrderService orderService;

    /**
     * 사용자 결제 완료 -> 주문 생성 -> 업주 페이지에 실시간 알림
     */
    
    // onwer가 조리 완료시 실행 메소드
    @PostMapping("/order/complete")
    public String completeOrder(OrderRequestDto dto) {
        // 1) DB에 주문 정보 저장 (MyBatis 사용 예시)
        Order order = orderService.saveOrder(dto);

        // 2) 업주(WebSocket 구독자)에게 실시간 메시지 전송
        //    예: /topic/store/{storeId} 로 발행
        String destination = "/topic/store/" + order.getStore().getStoreId();
        simpMessagingTemplate.convertAndSend(destination, order);

        return "주문이 성공적으로 완료되었습니다.";
    }
}