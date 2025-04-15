package com.soldesk6F.ondal.owner.order;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.soldesk6F.ondal.useract.order.dto.OrderRequestDto;
import com.soldesk6F.ondal.useract.order.dto.OrderRequestDto.OrderDetailDto;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderStatus;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.repository.StoreRepository; // 예시
import com.soldesk6F.ondal.useract.order.repository.OrderRepository; // 예시

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    // 필요한 경우 OrderDetailRepository, UserRepository 등 추가로 주입
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 1) 주문 DTO -> Order 엔티티 변환
     * 2) Store, User 등 DB에서 조회 후 연관관계 연결
     * 3) OrderDetail 리스트를 추가
     * 4) 저장 & 리턴
     */
    public Order saveOrder(OrderRequestDto requestDto) {
        // 1) storeId로 Store 조회 (JPA 예시)
        Store store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(() -> new RuntimeException("가게를 찾을 수 없습니다. storeId=" + requestDto.getStoreId()));

        // 2) Order 엔티티 생성 (Builder 사용)
        Order order = Order.builder()
                .store(store)
                .deliveryAddress(requestDto.getDeliveryAddress())
                .storeRequest(requestDto.getStoreRequest())
                .deliveryRequest(requestDto.getDeliveryRequest())
                .orderAdditional1(requestDto.getOrderAdditional1())
                .orderAdditional2(requestDto.getOrderAdditional2())
                .orderStatus(OrderStatus.PENDING) // 기본값
                .build();

        // 3) OrderDetail 추가
        if (requestDto.getOrderDetails() != null && !requestDto.getOrderDetails().isEmpty()) {
            for (OrderDetailDto detailDto : requestDto.getOrderDetails()) {
                // OrderDetail 엔티티 생성
                OrderDetail orderDetail = new OrderDetail();
                // 예시: 메뉴 식별자 menuId를 별도로 저장할 수 있도록 OrderDetail 엔티티를 설계하거나,
                //       추후 menuId로 Menu 엔티티를 찾아서 연결할 수도 있음.

                // 수량, 가격, 옵션 등 세팅
                orderDetail.setQuantity(detailDto.getQuantity());
                orderDetail.setPrice(detailDto.getPrice());
                // detailDto.getOptionNames(), detailDto.getOptionPrices() 등도
                // OrderDetail or 별도 Option 엔티티에 맞춰 저장 로직을 작성하세요.

                // Order 엔티티에 추가 -> orderDetails에도 연결됨
                order.addOrderDetail(orderDetail);
            }
        }

        // 4) DB 저장
        //     - orderDetails의 cascade = CascadeType.ALL 이라면
        //       orderRepository.save(order) 시 orderDetails도 함께 저장됨
        Order savedOrder = orderRepository.save(order);

        return savedOrder;
    }
    
    @Transactional
    public Order updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        // 주문 상태 변경
        order.setOrderStatus(newStatus);
        // orderRepository.save(order) -> 영속성 컨텍스트가 flush될 때 반영됨
        Order savedOrder = orderRepository.save(order);

        // (1) 유저 채널로 알림
        if (savedOrder.getUser() != null) {
            String userId = savedOrder.getUser().getUserUuidAsString(); 
            // 혹은 savedOrder.getUser().getId() 등 실제 필드
            String userDestination = "/topic/user/" + userId;
            messagingTemplate.convertAndSend(userDestination, savedOrder);
        }

        // (2) 라이더 채널로 알림
        if (savedOrder.getRider() != null) {
            String riderId = savedOrder.getRider().getRiderUuidAsString(); 
            // 혹은 savedOrder.getRider().getId()
            String riderDestination = "/topic/rider/" + riderId;
            messagingTemplate.convertAndSend(riderDestination, savedOrder);
        }

        return savedOrder;
    }
    
    public Order findOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }
    
    @Transactional(readOnly = true)
    public List<Order> findAllByOwner() {
        // 실제로는 로그인한 업주의 storeId로 필터링
        // return orderRepository.findAllByStoreId(...);
        return orderRepository.findAll();
    }
}