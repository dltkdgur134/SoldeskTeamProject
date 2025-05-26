package com.soldesk6F.ondal.chat.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.chat.dto.ChatMessageDto;
import com.soldesk6F.ondal.chat.dto.ChatResponseDto;
import com.soldesk6F.ondal.chat.entity.ChatMessage;
import com.soldesk6F.ondal.chat.entity.ChatMessage.SenderType;
import com.soldesk6F.ondal.chat.entity.ChatRoom;
import com.soldesk6F.ondal.chat.repository.ChatMessageRepository;
import com.soldesk6F.ondal.chat.repository.ChatRoomRepository;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;


@Service
public class ChatService {
	
	private final ChatMessageRepository chatMessageRepository;
	private final ChatRoomRepository chatRoomRepository;
	
	private final OrderRepository orderRepository;
	
	public ChatService(ChatMessageRepository chatMessageRepository, 
						ChatRoomRepository chatRoomRepository, 
						OrderRepository orderRepository) {
		this.chatMessageRepository = chatMessageRepository;
		this.chatRoomRepository = chatRoomRepository;
		this.orderRepository = orderRepository;
	}
	
	// 채팅방 생성 or 찾기
	@Transactional
	public ChatRoom saveChatroom(Order order) {
		Optional<ChatRoom> findChatRoom = chatRoomRepository.findByOrderId(order.getOrderId());
		// 채팅방이 존재하지 않으면 새로 만들기
		if (findChatRoom.isEmpty()) {
			ChatRoom chatRoom = ChatRoom.builder()
					.build();
			chatRoom.setOrderId(order.getOrderId());
			chatRoomRepository.save(chatRoom);
			return chatRoom;
		} 
		// 채팅방이 존재하면 불러오기
		return findChatRoom.get();
	}
	
	// 채팅 메시지 저장
	@Transactional
	public ChatMessage saveMessage(ChatRoom chatRoom, User sender, ChatMessageDto chatMessageDto) {
		
		String message = chatMessageDto.getText();
		String senderTypeString = chatMessageDto.getSenderType();
		
		ChatMessage chatMessage = ChatMessage.builder()
	                .chatRoom(chatRoom)
	                .sender(sender)
	                .message(message)
	                .build();
		
		switch (senderTypeString) {
			case "손님": {
				chatMessage.setSenderType(SenderType.USER);
				break;
			}
			case "사장님": {
				chatMessage.setSenderType(SenderType.OWNER);
				break;
			}
			case "라이더": {
				chatMessage.setSenderType(SenderType.RIDER);
				break;
			}
			default:
				break;
		}
		
		chatMessageRepository.save(chatMessage);
	    return chatMessage;
	}
	
	// 유저용 채팅 메시지 불러오기
	@Transactional(readOnly = true)
	public List<ChatResponseDto> getChatMessage(String orderId) {
		UUID orderUuid = UUID.fromString(orderId);
		
	    Optional<Order> order = orderRepository.findById(orderUuid);

	    if (order.isEmpty()) {
	        return Collections.emptyList();
	    }

	    Optional<ChatRoom> chatRoom = chatRoomRepository.findByOrderId(order.get().getOrderId());

	    if (chatRoom.isEmpty()) {
	        return Collections.emptyList();
	    }
	    
	    List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoom(chatRoom.get());
	    
	    
	   List<ChatResponseDto> previousChatMessages = chatMessages.stream()
	        .map(this::toChatResponseDto)
	        .collect(Collectors.toList());
	   return previousChatMessages;
	}
	
	 private ChatResponseDto toChatResponseDto(ChatMessage chatMessage) {
		 var dto = new ChatResponseDto(chatMessage);
		 return dto;
	 }
	 
}
