package com.soldesk6F.ondal.chat.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.soldesk6F.ondal.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id", nullable = false, unique = true)
    private Long chatMessageId;  // 채팅 메시지 ID (PK)

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;  // 채팅방 ID (FK)

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;  // 메시지 발신자

    @Lob
    @Column(name = "message", nullable = false)
    private String message;  // 메시지 내용

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;  // 메시지 전송 시간

    @Builder
    public ChatMessage(ChatRoom chatRoom, User sender, String message) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.message = message;
    }
    
    public String getChatMessageUuidAsString() {
	    return chatMessageId != null ? chatMessageId .toString() : null;
	}
    
}
