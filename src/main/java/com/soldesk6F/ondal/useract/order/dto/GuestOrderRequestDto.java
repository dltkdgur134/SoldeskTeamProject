package com.soldesk6F.ondal.useract.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class GuestOrderRequestDto {
    private UUID storeId;
    private String deliveryAddress;
    private String storeRequest;
    private String deliveryRequest;
    private String orderAdditional1;
    private String orderAdditional2;
    private List<OrderDetailDto> orderDetails;

    @Getter
    @Setter
    public static class OrderDetailDto {
        private UUID menuId;
        private int quantity;
        private int price;
        private List<String> optionNames;
        private List<Integer> optionPrices;
    }
}
