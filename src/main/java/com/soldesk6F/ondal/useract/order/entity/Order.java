package com.soldesk6F.ondal.useract.order.entity;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")  // "order"는 SQL 예약어라서 "orders"로 변경
public class Order {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "order_id", updatable = false, nullable = false, unique = true)
    private UUID orderId;  // 기본키 (UUID 사용)

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 주문한 사용자 (FK)

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;  // 주문한 가게 (FK)

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;  // 배달원 (FK) (배정될 수도, 안 될 수도 있음)

    @CreationTimestamp
    @Column(name = "order_time", nullable = false, updatable = false)
    private LocalDateTime orderTime;  // 주문 시간 (자동 저장)

    @Column(name = "delivery_address", nullable = false, length = 255)
    private String deliveryAddress;  // 배달 주소

    @Lob
    @Column(name = "store_request")
    private String storeRequest;  // 가게 요청사항
    
    @Lob
    @Column(name = "delivery_request")
    private String deliveryRequest;  // 배달 요청사항

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;  // 주문 상태

    @Column(name = "total_price", nullable = false)
    private int totalPrice;  // 총 주문 금액

    @Column(name = "order_additional1", length = 255)
    private String orderAdditional1;  // 추가 옵션 1

    @Column(name = "order_additional2", length = 255)
    private String orderAdditional2;  // 추가 옵션 2
 
    // 🔹 주문 상세 목록 추가 (OrderDetail과 연관 관계 설정)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();  // 주문 상세 목록

    public enum OrderStatus {
    	PENDING,  // 주문 접수 대기
    	CONFIRMED,  // 주문 확인됨
    	IN_DELIVERY,  // 배달 중
    	COMPLETED,  // 배달 완료
    	CANCELED  // 주문 취소
    }

    
    
    @Builder
    public Order(User user, Store store, String deliveryAddress, String storeRequest,
            String deliveryRequest, int totalPrice, String orderAdditional1, String orderAdditional2,
            OrderStatus orderStatus) {
   this.user = user;
   this.store = store;
   this.deliveryAddress = deliveryAddress;
   this.storeRequest = storeRequest;
   this.deliveryRequest = deliveryRequest;
   this.totalPrice = totalPrice;
   this.orderAdditional1 = orderAdditional1;
   this.orderAdditional2 = orderAdditional2;
   this.orderStatus = OrderStatus.PENDING;
}

    // ✅ 주문 상세를 추가하면서 자동으로 총 가격 업데이트
    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
        updateTotalPrice();
    }

    // ✅ 총 가격 업데이트 메서드
    public void updateTotalPrice() {
        this.totalPrice = orderDetails.stream()
                .mapToInt(od -> od.getQuantity() * od.getPrice())
                .sum();
    }
    
    
}

