package com.soldesk6F.ondal.useract.order.dto;

import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToOwner;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
import com.soldesk6F.ondal.useract.order.entity.OrderStatus;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
public class OrderResponseDto {
	
	private OrderToOwner orderToOwner;
    private UUID orderId;
    private String deliveryAddress;	
    private String storeRequest;
    private String deliveryRequest;
    private int totalPrice;
    private LocalDateTime orderTime;
    private LocalDateTime cookingStartTime;
    private LocalTime expectCookingTime;

    private List<OrderDetailDto> orderDetails;

    @Getter
    @Builder
    public static class OrderDetailDto {
        private UUID orderDetailId;
        private String menuName;
        private int quantity;
        private int price;
        private List<String> optionNames;
        private List<Integer> optionPrices;
    }

    public static OrderResponseDto from(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderToOwner(order.getOrderToOwner()) // ⚠️ orderStatus가 null이면 NPE 주의
                .deliveryAddress(order.getDeliveryAddress())
                .storeRequest(order.getStoreRequest())
                .deliveryRequest(order.getDeliveryRequest())
                .totalPrice(order.getTotalPrice())
                .orderTime(order.getOrderTime())
                .expectCookingTime(order.getExpectCookingTime())
                .cookingStartTime(order.getCookingStartTime())
                .orderDetails(
                    order.getOrderDetails().stream()
                    .map((OrderDetail detail) -> {
                        return OrderDetailDto.builder()
                            .orderDetailId(detail.getOrderDetailId())
                            .menuName(detail.getMenu().getMenuName())
                            .quantity(detail.getQuantity())
                            .price(detail.getPrice())
                            .optionNames(detail.getOptionNames())
                            .optionPrices(detail.getOptionPrices())
                            .build();
                    })
                    .collect(Collectors.toList()) // ✅ 꼭 필요!
                )
                .build();
    }

}