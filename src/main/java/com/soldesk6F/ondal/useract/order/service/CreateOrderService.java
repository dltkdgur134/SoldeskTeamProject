package com.soldesk6F.ondal.useract.order.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class CreateOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public Order createOrder(Order order) {
        order.calculateDeliveryFee();  // 배달료 계산
        order.updateTotalPrice();  // 총 가격 업데이트
        
        LocalDate today = LocalDate.now();
        
        Integer maxTodayOrderNumber = orderRepository.findMaxOrderNumberForToday(today);

        int nextOrderNumber = (maxTodayOrderNumber != null && maxTodayOrderNumber < 999)
            ? maxTodayOrderNumber + 1
            : 1;
        
        // Builder 패턴을 사용해 Order 객체를 생성
        Order newOrder = Order.builder()
                .user(order.getUser())  // User 설정
                .guestId(order.getGuestId())  // guestId 설정
                .store(order.getStore())  // Store 설정
                .rider(order.getRider())  // Rider 설정
                .expectCookingTime(order.getExpectCookingTime())  // 예상 조리 시간 설정
                .cookingStartTime(order.getCookingStartTime())  // 조리 시작 시간 설정
                .realCookingTime(order.getRealCookingTime())  // 실제 조리 시간 설정
                .deliveryStartTime(order.getDeliveryStartTime())  // 배달 시작 시간 설정
                .expectDeliveryTime(order.getExpectDeliveryTime())  // 예상 배달 시간 설정
                .realDeliveryTime(order.getRealDeliveryTime())  // 실제 배달 시간 설정
                .deliveryAddress(order.getDeliveryAddress())  // 배달 주소 설정
                .deliveryAddressLatitude(order.getDeliveryAddressLatitude())  // 배달 주소 위도 설정
                .deliveryAddressLongitude(order.getDeliveryAddressLongitude())  // 배달 주소 경도 설정
                .deliveryFee(order.getDeliveryFee())  // 배달료 설정
                .storeRequest(order.getStoreRequest())  // 가게 요청사항 설정
                .deliveryRequest(order.getDeliveryRequest())  // 배달 요청사항 설정
                .orderToOwner(order.getOrderToOwner())  // 가게에 대한 주문 상태 설정
                .cancledWhy(order.getCancledWhy())  // 취소 이유 설정
                .orderToRider(order.getOrderToRider())  // 라이더에 대한 주문 상태 설정
                .totalPrice(order.getTotalPrice())  // 총 가격 설정
                .orderAdditional1(order.getOrderAdditional1())  // 추가사항 1 설정
                .orderAdditional2(order.getOrderAdditional2())  // 추가사항 2 설정
                .orderDetails(order.getOrderDetails())  // 주문 상세 설정
                .build();

        return orderRepository.save(newOrder);  // 새로 생성된 주문 객체를 저장
    }
}

