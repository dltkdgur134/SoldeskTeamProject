package com.soldesk6F.ondal.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.chat.entity.ChatMessage;
import com.soldesk6F.ondal.chat.entity.ChatRoom;

import java.util.List;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	List<ChatMessage> findByChatRoom(ChatRoom chatRoom);
	
	
}
