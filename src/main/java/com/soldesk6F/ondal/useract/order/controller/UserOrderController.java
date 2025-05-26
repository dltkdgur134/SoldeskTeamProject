package com.soldesk6F.ondal.useract.order.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk6F.ondal.owner.order.OrderService;
import com.soldesk6F.ondal.owner.order.dto.OrderLiveDto;
import com.soldesk6F.ondal.useract.order.dto.OrderHistoryDto;
import com.soldesk6F.ondal.useract.order.dto.OrderInfoDetailDto;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToRider;

@Controller
@RequestMapping("/user/order")
public class UserOrderController {
	
	private final OrderService orderService;

	
    /** application.properties 에 kakao.maps.app-key=YOUR_APP_KEY 로 두셨다면 */
    @Value("${kakao.maps.app-key}")
    private String kakaoAppKey;

    public UserOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    public String viewOrder(
            @PathVariable("orderId") String orderId,
            @AuthenticationPrincipal(expression="user.userId") String userId,
            Model model
    ) {
        // 1) 사용자 주문 정보 로드 (권한 체크 포함)
        OrderToRider status = orderService.	getOrderToRider(orderId);

        if (status == OrderToRider.COMPLETED) {
            // 2) 완료된 주문 → orderInfo 페이지
//            OrderHistoryDto dto = orderService.getOrderHistoryDto(orderId);
        	OrderInfoDetailDto dto = orderService.getOrderInfoDetailDto(orderId);
            model.addAttribute("order", dto);
            return "content/orderInfo";
        } else {
            // 3) 진행중 주문 → orderLive 페이지
        	OrderLiveDto dto = orderService.toOrderLiveDto(orderId);
        	model.addAttribute("order", dto);
        	return "content/orderLive";
        }
    }
    
}
