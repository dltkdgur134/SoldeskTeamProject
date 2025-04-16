package com.soldesk6F.ondal.useract.order.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderResponseDto {
    private UUID orderId;
    private String deliveryAddress;
    private String storeRequest;
    private String deliveryRequest;
    private String orderStatus;
    private int totalPrice;
    private LocalDateTime orderTime;

    private List<OrderDetailDto> orderDetails;

    @Getter @Setter
    public static class OrderDetailDto {
        private String menuName;
        private int quantity;
        private int price;
        private List<String> optionNames;
    }
}