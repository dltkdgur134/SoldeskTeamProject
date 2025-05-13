package com.soldesk6F.ondal.useract.payment.dto;


import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TossPaymentResponse {

    private String paymentKey;
    private String orderId;
    private String status;
    private int totalAmount;
    private OffsetDateTime requestedAt;
    private OffsetDateTime approvedAt;
    private String method;
    

    @JsonProperty("metadata") // JSON 키 이름과 변수 이름이 다를 경우 지정
    private MetaData metadata;
    
    @Data
    public static class MetaData {
    	private String reqDel;
    	private String reqStore;
    	
    }
    
    
    
    
}
