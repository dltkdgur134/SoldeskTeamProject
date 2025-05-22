package com.soldesk6F.ondal.useract.order.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.jaxb.SpringDataJaxb.OrderDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;
import com.soldesk6F.ondal.useract.order.service.OrderQueryService;

import lombok.RequiredArgsConstructor;

@RestController                   // ← View 렌더링 대신 JSON 직렬화
@RequestMapping("/user/order")    // ex) /user/order/active-ids
@RequiredArgsConstructor
public class OrderRestController {
	
	private final OrderQueryService orderQueryService;
	
    @GetMapping("/active-ids")
    public List<UUID> activeOrderIds(@AuthenticationPrincipal CustomUserDetails user) {
        return orderQueryService.getActiveOrderIds(user.getUserUuid());
    }
    
//    @GetMapping("/{orderId}")
//    public OrderDto orderDetail(@PathVariable UUID orderId,
//                                @AuthenticationPrincipal(expression = "userUuid") UUID userUuid) {
//        return orderQueryService.getDetailForUser(orderId, userUuid);
//    }
}
