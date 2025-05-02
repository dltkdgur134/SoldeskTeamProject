package com.soldesk6F.ondal.user.controller.rider;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.Rider;
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
	@Autowired
	private RiderService riderService;
	
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
	
	// 배달 시작 페이지로 이동 및 OrderToRider 값 변경
	@PostMapping("/deliveryStart")
	public String startDelivery(@RequestParam("orderId") UUID orderId, Model model) {
	    // 주문 조회
	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
	    
	    // 주문 상태가 DISPATCHED일 경우에만 배달 시작 가능
	    if (order.getOrderToRider() == Order.OrderToRider.DISPATCHED) {
	        order.setOrderToRider(Order.OrderToRider.ON_DELIVERY);
	        orderRepository.save(order);  // 상태 변경 후 저장
	    } else {
	        // 상태 변경 불가 메시지 처리 (예: 상태가 이미 ON_DELIVERY인 경우)
	        throw new RuntimeException("배달 시작이 불가능한 상태입니다.");
	    }
	     model.addAttribute("orderId", orderId);
		 return "content/rider/deliveryStart";  // Thymeleaf의 템플릿 이름
	}
	// 배달 완료 후 다시 riderHome 요청 및 OrderToRider 값 변경 (riderWallet에 배달료 만큼 추가) 
	@PostMapping("/deliveryFin")
	public String finishDelivery(@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("orderId") UUID orderId,Model model) {
		
		String userId = userDetails.getUser().getUserId();
		Optional<Rider> optionalRider = riderRepository.findByUser_UserId(userId);
	    
		if (optionalRider.isPresent()) {
            Rider rider = optionalRider.get();
            model.addAttribute("riderId", rider.getRiderId());
            if(rider.getRiderStatus() == Rider.RiderStatus.DELIVERING){
            	riderService.completeOrderAndRewardRider(orderId);
            	rider.setRiderStatus(Rider.RiderStatus.WAITING);
            	riderRepository.save(rider);
            }
        } else {
            // 예외 처리나 에러 페이지로 이동 가능
        }
		
		
		// 주문 조회
	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

	    // 주문 상태가 ON_DELIVERY일 경우에만 완료 가능
	    if (order.getOrderToRider() == Order.OrderToRider.ON_DELIVERY) {
	        order.setOrderToRider(Order.OrderToRider.COMPLETED);
	        orderRepository.save(order);  // 상태 변경 후 저장
	    } else {
	        // 상태 변경 불가 메시지 처리 (예: 상태가 DISPATCHED 또는 이미 COMPLETED인 경우)
	        throw new RuntimeException("배달 완료가 불가능한 상태입니다.");
	    }

	    return "redirect:/rider/home";  
	}
	
	
}
