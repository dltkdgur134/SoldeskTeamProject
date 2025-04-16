package com.soldesk6F.ondal.useract.order.entity;


import java.time.LocalDateTime;
import java.time.LocalTime;
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
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")  // 'order'는 예약어이므로 'orders' 사용
public class Order {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "order_id", updatable = false, nullable = false, unique = true)
    private UUID orderId;

    @ManyToOne
    @JoinColumn(name = "user_uuid", nullable = true)
    private User user;

    @Column(name = "guest_id", length = 36)
    private String guestId;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;

    @CreationTimestamp
    @Column(name = "order_time", nullable = false, updatable = false)
    private LocalDateTime orderTime;

    @Column(name = "expect_cooking_time")
    private LocalTime expectCookingTime;
    
    @Column(name = "real_cooking_time", updatable = false)
    private LocalTime realCookingTime;
    
    @Column(name = "expect_delivery_time")
    private LocalTime expectDeliveryTime;
    
    @Column(name = "real_delivery_time", updatable = false)
    private LocalTime realDeliveryTime;
    
    @Column(name = "delivery_address", nullable = false, length = 255)
    private String deliveryAddress;

    @Lob
    @Column(name = "store_request")
    private String storeRequest;

    @Lob
    @Column(name = "delivery_request")
    private String deliveryRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(name = "order_additional1", length = 255)
    private String orderAdditional1;

    @Column(name = "order_additional2", length = 255)
    private String orderAdditional2;

    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public enum OrderStatus {
        PENDING("주문 요청 중"), 
        CONFIRMED("주문 확인 완료"), 
        IN_DELIVERY("배달 중"), 
        COMPLETED("주문 및 결재 완료"), 
        CANCELED("주문 취소");
    	private final String description;
    	OrderStatus(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
    }

    @Builder
    public Order(User user, Store store, String deliveryAddress, String storeRequest,
                 String deliveryRequest, int totalPrice, String orderAdditional1, String orderAdditional2,
                 OrderStatus orderStatus, String guestId) {
        this.user = user;
        this.store = store;
        this.deliveryAddress = deliveryAddress;
        this.storeRequest = storeRequest;
        this.deliveryRequest = deliveryRequest;
        this.totalPrice = totalPrice;
        this.orderAdditional1 = orderAdditional1;
        this.orderAdditional2 = orderAdditional2;
        this.orderStatus = orderStatus != null ? orderStatus : OrderStatus.PENDING;
        this.guestId = guestId;
        
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
        updateTotalPrice();
    }

    public void updateTotalPrice() {
       this.totalPrice = calculateOrderTotal();
    }
    
    public int calculateOrderTotal() {
        return orderDetails.stream()
        		.mapToInt(od -> od.getQuantity() * od.getPrice())
        		.sum();
    }
    
    public String getOrderUuidAsString() {
	    return orderId != null ? orderId .toString() : null;
	}
    
    
}

