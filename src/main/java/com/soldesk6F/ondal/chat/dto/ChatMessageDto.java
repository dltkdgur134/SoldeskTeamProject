package com.soldesk6F.ondal.chat.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ChatMessageDto {
    private UUID orderId;
    private String senderId;       // 보낸 사람 (userId or storeId or riderId)
    private String senderType;     // USER, STORE, RIDER
    private String senderName;
    private String text;           // 메세지 본문
    private String timestamp;      // ISO String
}
