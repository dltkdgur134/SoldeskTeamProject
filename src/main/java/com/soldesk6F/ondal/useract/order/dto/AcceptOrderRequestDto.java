package com.soldesk6F.ondal.useract.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AcceptOrderRequestDto {
    private UUID orderId;         // 접수할 주문의 ID
    private int completionTime;   // 예상 조리 시간 (분 단위)
}
