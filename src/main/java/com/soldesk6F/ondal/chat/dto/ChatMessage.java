package com.soldesk6F.ondal.chat.dto;

import lombok.Data;

@Data
public class ChatMessage {
    private String storeId;
    private String orderId;
    private String sender;
    private String text;
    private String timestamp;
}
