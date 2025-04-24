package com.soldesk6F.ondal.useract.order.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ExtendTimeRequestDto {
    private UUID orderId;  // 시간 추가할 주문의 ID
    private int minutes;   // 추가할 시간 (분 단위)
}