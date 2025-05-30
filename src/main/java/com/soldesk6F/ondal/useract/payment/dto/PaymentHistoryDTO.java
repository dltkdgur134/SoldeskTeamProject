package com.soldesk6F.ondal.useract.payment.dto;
import java.time.LocalDateTime;

import com.soldesk6F.ondal.useract.order.entity.Order.OrderToOwner;
import com.soldesk6F.ondal.useract.payment.entity.Payment.PaymentMethod;
import com.soldesk6F.ondal.useract.payment.entity.Payment.PaymentStatus;
import com.soldesk6F.ondal.useract.payment.entity.Payment.PaymentUsageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentHistoryDTO {
    
    private PaymentMethod paymentMethod;
    private String paymentKey;
    private int amount;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private PaymentStatus paymentStatus;
    private String refundReason;
    private PaymentUsageType paymentUsageType;
    private String tossOrderId; // 선택적: 외부 조회용, 필요시 제거 가능
    private LocalDateTime paymentTime;
    private LocalDateTime updatedDate;
    
    private OrderToOwner orderToOwner;
    
    public static PaymentHistoryDTO fromEntity(com.soldesk6F.ondal.useract.payment.entity.Payment payment,
    		 OrderToOwner orderToOwner) {
        return PaymentHistoryDTO.builder()
                .paymentMethod(payment.getPaymentMethod())
                .paymentKey(payment.getPaymentKey())
                .amount(payment.getAmount())
                .requestedAt(payment.getRequestedAt())
                .approvedAt(payment.getApprovedAt())
                .paymentStatus(payment.getPaymentStatus())
                .refundReason(payment.getRefundReason())
                .paymentUsageType(payment.getPaymentUsageType())
                .tossOrderId(payment.getTossOrderId())
                .paymentTime(payment.getPaymentTime())
                .updatedDate(payment.getUpdatedDate())
                .orderToOwner(orderToOwner)
                .build();
    }
}

