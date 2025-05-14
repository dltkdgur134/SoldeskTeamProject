package com.soldesk6F.ondal.useract.payment.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TossPaymentResponse {

    private String paymentKey;
    private String orderId;
    private String status;
    private String approvedAt;
    private String method;
    private int totalAmount;

    @JsonProperty("metadata") // JSON 키 이름과 변수 이름이 다를 경우 지정
    private MetaData metadata;
    
    @Data
    public static class MetaData {
    	private String reqDel;
    	private String reqStore;
    	
    }

}
