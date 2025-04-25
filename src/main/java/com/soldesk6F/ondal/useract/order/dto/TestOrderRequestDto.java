package com.soldesk6F.ondal.useract.order.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class TestOrderRequestDto {
    private UUID storeId;
    private UUID menuId;
    private int quantity;
}
