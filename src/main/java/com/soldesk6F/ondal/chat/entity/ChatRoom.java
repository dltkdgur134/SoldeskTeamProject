package com.soldesk6F.ondal.chat.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.soldesk6F.ondal.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id", nullable = false, unique = true)
    private Long chatRoomId;  // 채팅방 ID (PK)

    @ManyToMany
    @JoinTable(
      name = "chat_room_participants",
      joinColumns = @JoinColumn(name = "chat_room_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants;  // 채팅방 참여자들 (여러 명)

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;  // 채팅방 생성 일시

    @Builder
    public ChatRoom(List<User> participants) {
        this.participants = participants;
        this.createdDate = LocalDateTime.now();
    }
    
    public String getChatRoomUuidAsString() {
	    return chatRoomId != null ? chatRoomId .toString() : null;
	}
}
