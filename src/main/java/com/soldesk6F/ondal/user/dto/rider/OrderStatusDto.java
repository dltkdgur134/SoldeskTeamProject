package com.soldesk6F.ondal.user.dto.rider;

import java.util.UUID;

import lombok.Data;

@Data
public class OrderStatusDto {

    private UUID orderId;
    private int currentStatus; // 1=수락, 2=조리, 3=배달중, 4=배달완료
    private String timestamp;
    private String orderStatus;
}
