package com.soldesk6F.ondal.useract.payment.dto;

import lombok.Data;

@Data
public class TossPaymentResponse {

    private String paymentKey;
    private String orderId;
    private String status;
    private int totalAmount;
    private Metadata metaData;

    @Data
    public static class Metadata{
    	private String reqDel;
    	private String reqStore;
    	
    }
    
    
    
    
}
