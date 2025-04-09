package com.soldesk6F.ondal.useract.order.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class GuestOrderResponseDto {

    private UUID orderId;
    private String deliveryAddress;
    private String storeRequest;
    private String deliveryRequest;
    private int totalPrice;
    private String storeName;
    private LocalDateTime orderDate;
    private List<OrderDetailDto> orderDetails;
    private String orderStatus;

    @Data
    public static class OrderDetailDto {
        private String menuName;
        private int quantity;
        private int price;
        private List<String> selectedOptions;
    }
}
