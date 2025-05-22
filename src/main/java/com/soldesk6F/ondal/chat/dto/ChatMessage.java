package com.soldesk6F.ondal.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ChatMessage {
	@JsonIgnore
    private String storeId;
    
    private String orderId;
    private String sender;
    private String text;
    private String timestamp;
}
