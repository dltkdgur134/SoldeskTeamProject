package com.soldesk6F.ondal.user.controller.rider;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soldesk6F.ondal.chat.dto.ChatMessageDto;
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.dto.rider.OrderStatusDto;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.RiderRepository;
import com.soldesk6F.ondal.user.service.RiderService;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/rider")
@RequiredArgsConstructor
public class DeliveryController {
	private final OrderRepository orderRepository;
	private final RiderRepository riderRepository;
	private final SimpMessagingTemplate messagingTemplate;
	private final RiderService riderService;
	
	 // 픽업 시작 페이지로 이동하는 메서드
	@GetMapping("/pickupStart/{orderId}")
    public String startPickUp(@PathVariable("orderId") UUID orderId, Model model,
    		@AuthenticationPrincipal CustomUserDetails userDetails
    		) {
		String userId = userDetails.getUser().getUserId();
		Optional<Rider> optionalRider = riderRepository.findByUser_UserId(userId);
		if (optionalRider.isPresent()) {
			Rider rider = optionalRider.get();
			riderService.assignRiderToOrder(orderId, rider.getRiderId());
		}
		
		model.addAttribute("orderId", orderId);
        // pickUpStart.html로 이동
		return "content/rider/pickUpStart";  // Thymeleaf의 템플릿 이름
    }
	
	// 배달 시작 페이지로 이동 및 OrderToRider , OrderToUser 값 변경
	@PostMapping("/deliveryStart")
	public String startDelivery(@RequestParam("orderId") UUID orderId,
	                            Model model,
	                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

	    if (order.getOrderToRider() == Order.OrderToRider.DISPATCHED) {
	        // 1. 주문 상태 변경
	        order.setOrderToRider(Order.OrderToRider.ON_DELIVERY);
	        order.setOrderToUser(Order.OrderToUser.DELIVERING);
	        order.setDeliveryStartTime(LocalDateTime.now());
	        orderRepository.save(order);

	        // 2. 채팅 메시지 생성 및 전송
	        User sender = customUserDetails.getUser();

	        ChatMessageDto message = new ChatMessageDto();
	        message.setOrderId(orderId);
	        message.setSenderId(sender.getUserUuidAsString());
	        message.setSenderName(sender.getUserId());             // ex. "rider01"
	        message.setSenderType("RIDER");
	        message.setText("배달을 시작했습니다.");
	        message.setTimestamp(LocalDateTime.now().toString());  // ISO 형식

	        UUID userUuid  = order.getUser().getUserUuid();
	        UUID storeUuid = order.getStore().getStoreId();
	        UUID riderUuid = order.getRider().getRiderId();

	        messagingTemplate.convertAndSendToUser(userUuid.toString(),  "/queue/chat", message);
	        messagingTemplate.convertAndSendToUser(storeUuid.toString(), "/queue/chat", message);
	        messagingTemplate.convertAndSendToUser(riderUuid.toString(), "/queue/chat", message);
	        messagingTemplate.convertAndSend("/topic/chat/" + orderId.toString(), message);
	        OrderStatusDto orderStatusDto = new OrderStatusDto();
	        orderStatusDto.setOrderId(orderId);
	        orderStatusDto.setCurrentStatus(3); // "배달 중" 단계
	        orderStatusDto.setTimestamp(LocalDateTime.now().toString());
	        orderStatusDto.setOrderStatus("배달 중");

	        messagingTemplate.convertAndSend("/topic/order/" + orderId.toString(), orderStatusDto);
	    
	    
	    } else {
	        throw new RuntimeException("배달 시작이 불가능한 상태입니다.");
	    }

	    model.addAttribute("orderId", orderId);
	    return "content/rider/deliveryStart";
	}
	//배달 완료 컨트롤러
	@PostMapping("/deliveryFin")
	public String finishDelivery(@AuthenticationPrincipal CustomUserDetails userDetails,
	                             @RequestParam("orderId") UUID orderId, Model model) {

	    riderService.completeOrderAndRewardRider(orderId);  // 단 한 줄만 호출

	    // 2. 채팅 메시지 전송
	    User sender = userDetails.getUser();

	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

	    ChatMessageDto message = new ChatMessageDto();
	    message.setOrderId(orderId);
	    message.setSenderId(sender.getUserUuidAsString());
	    message.setSenderName(sender.getUserId());
	    message.setSenderType("RIDER");
	    message.setText("배달을 완료했습니다.");
	    message.setTimestamp(LocalDateTime.now().toString());  // ISO 형식

	    UUID userUuid  = order.getUser().getUserUuid();
	    UUID storeUuid = order.getStore().getStoreId();
	    UUID riderUuid = order.getRider().getRiderId();

	    messagingTemplate.convertAndSendToUser(userUuid.toString(),  "/queue/chat", message);
	    messagingTemplate.convertAndSendToUser(storeUuid.toString(), "/queue/chat", message);
	    messagingTemplate.convertAndSendToUser(riderUuid.toString(), "/queue/chat", message);
	    messagingTemplate.convertAndSend("/topic/chat/" + orderId.toString(), message);
	    OrderStatusDto orderStatusDto = new OrderStatusDto();
        orderStatusDto.setOrderId(orderId);
        orderStatusDto.setCurrentStatus(4); // "배달 완료" 단계
        orderStatusDto.setTimestamp(LocalDateTime.now().toString());
        orderStatusDto.setOrderStatus("배달 완료");

        messagingTemplate.convertAndSend("/topic/order/" + orderId.toString(), orderStatusDto);
	    return "redirect:/rider/home";
	}

	
	
}
