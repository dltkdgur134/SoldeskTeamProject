package com.soldesk6F.ondal.useract.payment.dto;


import com.soldesk6F.ondal.useract.payment.entity.Payment.PaymentMethod;
import com.soldesk6F.ondal.useract.payment.entity.Payment.PaymentUsageType;
import com.soldesk6F.ondal.useract.payment.entity.Payment.PaymentStatus;

import lombok.Data;

@Data
public class OndalPayChargeRequest {
    private int amount; 
    private PaymentMethod paymentMethod = PaymentMethod.ONDALPAY; 
    private PaymentUsageType paymentUsageType = PaymentUsageType.ONDAL_WALLET; 
    private PaymentStatus paymentStatus = PaymentStatus.COMPLETED; 
    private String tossOrderId; 
}

