package com.soldesk6F.ondal.chat.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.soldesk6F.ondal.chat.entity.ChatMessage;
import com.soldesk6F.ondal.chat.entity.ChatMessage.SenderType;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ChatResponseDto {
	private Long chatMessageId;
	private Long chatRoomId;
	private UUID senderId;
	private SenderType senderType;
	private String message;
	private String timestamp;
	
	public ChatResponseDto (ChatMessage chatMessage) {
		this.chatMessageId = chatMessage.getChatMessageId();
		this.chatRoomId = chatMessage.getChatRoom().getChatRoomId();
		this.senderId = chatMessage.getSender().getUserUuid();
		this.senderType = chatMessage.getSenderType();
		this.message = chatMessage.getMessage();
		this.timestamp = chatMessage.getTimestamp().toString();
	}
	
}
