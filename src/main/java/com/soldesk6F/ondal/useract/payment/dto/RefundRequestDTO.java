package com.soldesk6F.ondal.useract.payment.dto;

import lombok.Data;

@Data
public class RefundRequestDTO {
    private String paymentKey;
    private String cancelReason;
}
