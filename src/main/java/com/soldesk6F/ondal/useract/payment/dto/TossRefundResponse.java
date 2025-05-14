package com.soldesk6F.ondal.useract.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TossRefundResponse {
	private String paymentKey;
	private String refundKey;
	private String status;
	private String reason;
	private String receiptKey;
	
}
