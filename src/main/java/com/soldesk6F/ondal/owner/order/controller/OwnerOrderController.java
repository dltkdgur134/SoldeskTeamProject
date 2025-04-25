package com.soldesk6F.ondal.owner.order.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.owner.order.OrderService;
import com.soldesk6F.ondal.useract.order.dto.AcceptOrderRequestDto;
import com.soldesk6F.ondal.useract.order.dto.ExtendTimeRequestDto;
import com.soldesk6F.ondal.useract.order.dto.OrderRequestDto;
import com.soldesk6F.ondal.useract.order.dto.OrderResponseDto;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToOwner;
import com.soldesk6F.ondal.useract.order.entity.OrderStatus;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/owner/order")
@RequiredArgsConstructor
public class OwnerOrderController {

    private final OrderService orderService;

    @PostMapping("/accept")
    public ResponseEntity<OrderResponseDto> acceptOrder(@RequestBody AcceptOrderRequestDto request, HttpSession session) {
        // 🟢 프론트에서 보낸 completionTime 값을 우선 사용
        int completionTime = request.getCompletionTime();

        // 🧠 주문 상태 업데이트 (expectCookingTime, cookingStartTime 포함)
        Order updatedOrder = orderService.acceptOrder(request.getOrderId(), completionTime);
        // 🔄 세션에 임시 저장된 cookingTime 제거 (있다면)
        Map<UUID, Integer> cookingTimes = (Map<UUID, Integer>) session.getAttribute("cookingTimes");
        if (cookingTimes != null) {
            cookingTimes.remove(request.getOrderId());
        }

        // 📨 JSON 응답
        return ResponseEntity.ok(OrderResponseDto.from(updatedOrder));
    }

    @PostMapping("/temp-cooking-time")
    public ResponseEntity<Void> storeTemporaryCookingTime(@RequestBody AcceptOrderRequestDto request, HttpSession session) {
        Map<UUID, Integer> cookingTimes = (Map<UUID, Integer>) session.getAttribute("cookingTimes");
        if (cookingTimes == null) {
            cookingTimes = new HashMap<>();
            session.setAttribute("cookingTimes", cookingTimes);
        }

        cookingTimes.put(request.getOrderId(), request.getCompletionTime());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/get-cooking-time")
    public ResponseEntity<Integer> getCookingTime(@RequestParam("orderId") UUID orderId, HttpSession session) {
        Map<UUID, Integer> cookingTimes = (Map<UUID, Integer>) session.getAttribute("cookingTimes");
        int time = (cookingTimes != null && cookingTimes.containsKey(orderId))
            ? cookingTimes.get(orderId)
            : 15;
        return ResponseEntity.ok(time);
    }
    
    @PostMapping("/complete")
    public ResponseEntity<OrderResponseDto> completeOrder(@RequestBody Map<String, String> payload) {
        UUID orderId = UUID.fromString(payload.get("orderId"));
        Order updated = orderService.completeOrder(orderId);
        return ResponseEntity.ok(OrderResponseDto.from(updated));
    }

    @PostMapping("/extendTime")
    public ResponseEntity<OrderResponseDto> extendTime(@RequestBody ExtendTimeRequestDto request) {
    	Order updated = orderService.extendCookingTime(request.getOrderId(), request.getMinutes());
        return ResponseEntity.ok(OrderResponseDto.from(updated)); // ✅ Dto로 변환해서 반환
    }

    @PostMapping("/reject")
    public ResponseEntity<Order> rejectOrder(@RequestParam("orderId") UUID orderId) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, OrderToOwner.CANCELED);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Order> cancelOrder(@RequestParam("orderId") UUID orderId) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, OrderToOwner.CANCELED);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @GetMapping("/detail")
    public ResponseEntity<OrderResponseDto> getOrderDetail(@RequestParam("orderId") UUID orderId) {
        Order order = orderService.findOrder(orderId);
        return ResponseEntity.ok(OrderResponseDto.from(order));
    }
    
    // 주문 전체 목록, 특정 상태 목록 등 조회용 API
    @GetMapping("/list")
    public ResponseEntity<List<OrderResponseDto>> getOrderList(HttpSession session) {
        UUID storeId = (UUID) session.getAttribute("storeId");
        if (storeId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Order> orders = orderService.getOrdersByStore(storeId);
        List<OrderResponseDto> dtoList = orders.stream()
            .map(OrderResponseDto::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }
    
    
    // test용 세션
    @GetMapping("/test-login")
    public String fakeLogin(HttpSession session) {
        // 테스트용 Store UUID (실제 DB에 있는 값으로 바꿔야 함)
        UUID dummyStoreId = UUID.fromString("7e603d19-5893-497d-ac93-8ea8f537c3d9");
        session.setAttribute("storeId", dummyStoreId);
        return "세션에 storeId 저장 완료: " + dummyStoreId;
    }
    
    private OrderResponseDto convertToDto(Order order) {
        List<OrderResponseDto.OrderDetailDto> detailDtos = order.getOrderDetails().stream().map(detail ->
            OrderResponseDto.OrderDetailDto.builder()
                .menuName(detail.getMenu() != null ? detail.getMenu().getMenuName() : "알 수 없음")
                .quantity(detail.getQuantity())
                .price(detail.getPrice())
                .optionNames(detail.getOptionNames() != null ? detail.getOptionNames() : List.of())
                .build()
        ).toList();

        return OrderResponseDto.builder()
            .orderId(order.getOrderId())
            .deliveryAddress(order.getDeliveryAddress())
            .storeRequest(order.getStoreRequest())
            .deliveryRequest(order.getDeliveryRequest())
            .orderToOwner(order.getOrderToOwner()) // enum 그대로
            .totalPrice(order.getTotalPrice())
            .orderTime(order.getOrderTime())
            .expectCookingTime(order.getExpectCookingTime()) // 누락 시 추가
            .orderDetails(detailDtos)
            .build();
    }
}