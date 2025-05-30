package com.soldesk6F.ondal.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.chat.entity.ChatRoom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	public Optional<ChatRoom> findByChatRoomId(Long chatRoomId);
	public Optional<ChatRoom> findByOrderId(UUID orderId);
}
