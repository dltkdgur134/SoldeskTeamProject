package com.soldesk6F.ondal.owner.order.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.OrderStatus;
import com.soldesk6F.ondal.store.entity.Store;

import java.util.UUID;

/**
 * 테스트용 컨트롤러 (실제 운영 시 삭제 or 주석처리)
 */
@RestController
@RequiredArgsConstructor
public class OrderTestController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    
    // 만약 DB 저장까지 확인하고 싶다면, OrderService, OrderRepository 등을 주입받아도 됩니다.
    
    @GetMapping("/test/order")
    public String testOrder() {
        // 1) 임의로 Order 객체 생성 (실제 DB 연동 없이 Mock 데이터)
        Order testOrder = new Order();
        
        // 예시 storeId 세팅 (실제 Store 엔티티가 DB에 있다고 가정)
        Store store = new Store();
        // store.setStoreId( UUID.randomUUID() );  // 실제 UUID 세팅 필요
        // 또는 DB에서 실제 매장을 불러오고 싶다면 StoreRepository findById() 등 사용
        testOrder.setStore(store);

        // 나머지 속성들 세팅
        testOrder.setOrderId(UUID.randomUUID());
        testOrder.setOrderStatus(OrderStatus.PENDING);
        testOrder.setDeliveryAddress("서울특별시 강남구 논현동 123-45");
        testOrder.setStoreRequest("매운맛으로 주세요");
        testOrder.setDeliveryRequest("벨 누르지 말고 문자 부탁합니다");
        testOrder.setTotalPrice(25000);
        
        // 2) 업주 페이지에 웹소켓 전송
        //    가정: ownerDashboard에서 /topic/store/{storeId}를 구독 중
        //    store 엔티티 내부에 storeId (UUID 등) 있다고 가정
        //    여기서는 단순히 "storeId=UUID-고정값"이라 치고 예시
        UUID dummyStoreId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String destination = "/topic/store/" + dummyStoreId;
        simpMessagingTemplate.convertAndSend(destination, testOrder);

        return "테스트 주문을 발행했습니다. storeId=" + dummyStoreId;
    }
}