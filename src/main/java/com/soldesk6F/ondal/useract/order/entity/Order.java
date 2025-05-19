package com.soldesk6F.ondal.useract.order.entity;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
    @JsonIgnoreProperties({"orders"})
    private User user;

    @Column(name = "guest_id", length = 36)
    private String guestId;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    @JsonIgnoreProperties({"orders", "owner"})
    private Store store;

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;

    @CreationTimestamp
    @Column(name = "order_time", nullable = false, updatable = false)
    private LocalDateTime orderTime;

    @Column(name = "expect_cooking_time")
    private LocalTime expectCookingTime;
    
    @Column(name = "cooking_start_time")
    private LocalDateTime cookingStartTime;
    
    @Column(name = "real_cooking_time")
    private LocalTime realCookingTime;
    
    @Column(name = "delivery_start_time")
    private LocalDateTime deliveryStartTime;
    
    @Column(name = "expect_delivery_time")
    private LocalTime expectDeliveryTime;
    
    @Column(name = "real_delivery_time")
    private LocalTime realDeliveryTime;
    
    @Column(name = "delivery_address", nullable = false, length = 255)
    private String deliveryAddress;

    @Column(name = "delivery_address_latitude", nullable = false)
    private double deliveryAddressLatitude;  // 배달 주소 위도 추가

    @Column(name = "delivery_address_longitude", nullable = false)
    private double deliveryAddressLongitude;  // 배달 주소 경도 추가


    @Column(name = "delivery_fee", nullable = false)
    private int deliveryFee;
    
    @Lob
    @Column(name = "store_request")
    private String storeRequest;

    @Lob
    @Column(name = "delivery_request")
    private String deliveryRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_to_owner", nullable = false)
    private OrderToOwner orderToOwner;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "cancled_why", nullable = true)
    private CancledWhy cancledWhy;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "order_to_rider", nullable = false)
    private OrderToRider orderToRider;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(name = "order_additional1", length = 255)
    private String orderAdditional1;

    @Column(name = "order_additional2", length = 255)
    private String orderAdditional2;

    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnoreProperties("order")
    private List<OrderDetail> orderDetails = new ArrayList<>();


    public enum OrderToOwner {
        PENDING("주문 요청 중"), 
        CONFIRMED("주문 확인 완료"),
        IN_DELIVERY("배달 중"), 
        COMPLETED("주문 완료"), 
        CANCELED("주문 취소");
    	private final String description;
    	OrderToOwner(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
    }
    public enum CancledWhy {
    	REQUEST_REFUSED("고객 요청 수용 불가"), 
    	MATERIALS_USED_UP("재료소진"),
    	CIRCUMSTANCES("업소 사정"), 
    	CANNOTDELIVERY("배달 불가");
    	private final String description;
    	CancledWhy(String description) {
    		this.description = description;
    	}
    	
    	public String getDescription() {
    		return description;
    	}
    }
    
    public enum OrderToRider {
        PENDING("주문 요청 중"), 
        CONFIRMED("주문 확인 완료"),
        DISPATCHED("라이더 배차 수락"), 
        ON_DELIVERY("픽업 완료 후 배달 중"),
        COMPLETED("배달 완료"),
        INTERRUPTED("배달 중단");
    	
    	private final String description;
    	OrderToRider(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
    }
    @Builder
    public Order(User user, String guestId, Store store, Rider rider, LocalTime expectCookingTime,
                 LocalDateTime cookingStartTime, LocalTime realCookingTime, LocalDateTime deliveryStartTime,
                 LocalTime expectDeliveryTime, LocalTime realDeliveryTime, String deliveryAddress,
                 Double deliveryAddressLatitude, Double deliveryAddressLongitude, int deliveryFee,
                 String storeRequest, String deliveryRequest, OrderToOwner orderToOwner, CancledWhy cancledWhy,
                 OrderToRider orderToRider, int totalPrice, String orderAdditional1, String orderAdditional2,
                 List<OrderDetail> orderDetails) {

        this.user = user;
        this.guestId = guestId;
        this.store = store;
        this.rider = rider;
        this.expectCookingTime = expectCookingTime;
        this.cookingStartTime = cookingStartTime;
        this.realCookingTime = realCookingTime;
        this.deliveryStartTime = deliveryStartTime;
        this.expectDeliveryTime = expectDeliveryTime;
        this.realDeliveryTime = realDeliveryTime;
        this.deliveryAddress = deliveryAddress;
        this.deliveryAddressLatitude = deliveryAddressLatitude;
        this.deliveryAddressLongitude = deliveryAddressLongitude;
        this.deliveryFee = deliveryFee;
        this.storeRequest = storeRequest;
        this.deliveryRequest = deliveryRequest;
        this.orderToOwner = orderToOwner;
        this.cancledWhy = cancledWhy;
        this.orderToRider = orderToRider;
        this.totalPrice = totalPrice;
        this.orderAdditional1 = orderAdditional1;
        this.orderAdditional2 = orderAdditional2;
        this.orderDetails = orderDetails;
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
                .mapToInt(OrderDetail::calculateTotalPrice)
                .sum();
    }
    
    public int getTotalPrice() {
        return calculateOrderTotal() + deliveryFee;
    }
    
    public String getOrderUuidAsString() {
	    return orderId != null ? orderId .toString() : null;
	}


    //출발지(가게주소)와 도착지(배달주소)의 위도 경도로 거리 계산
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
    // 그 거리 기반으로 배달료 측정
    public void calculateDeliveryFee() {
    	double storeLat = store.getStoreLatitude();
    	double storeLon = store.getStoreLongitude();
    	double distance = calculateDistance(storeLat, storeLon, deliveryAddressLatitude, deliveryAddressLongitude);
    	
    	if (distance < 1.0) { 
    		this.deliveryFee = 3000;
    	} else if (distance < 2.0) {
    		this.deliveryFee = 4000;
    	} else if (distance < 3.0) {
    		this.deliveryFee = 5000;
    	} else {
    		this.deliveryFee = 6000;
    	}
    }
    
    @Transient
    public LocalDateTime getCookingEndTime() {
        if (cookingStartTime == null || expectCookingTime == null) {
            return null;
        }
        return cookingStartTime
            .plusHours(expectCookingTime.getHour())
            .plusMinutes(expectCookingTime.getMinute())
            .plusSeconds(expectCookingTime.getSecond());
    }
    
    @Transient
    public LocalDateTime getDeliveryCompleteTime() {
        if (deliveryStartTime == null || realDeliveryTime == null) {
            return null;
        }
        return deliveryStartTime
            .plusHours(realDeliveryTime.getHour())
            .plusMinutes(realDeliveryTime.getMinute())
            .plusSeconds(realDeliveryTime.getSecond());
    }
}


