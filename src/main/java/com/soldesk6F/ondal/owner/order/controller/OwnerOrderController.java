package com.soldesk6F.ondal.owner.order.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.owner.order.OrderService;
import com.soldesk6F.ondal.useract.order.dto.OrderResponseDto;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/owner/order")
@RequiredArgsConstructor
public class OwnerOrderController {

    private final OrderService orderService;

    @PostMapping("/accept")
    public ResponseEntity<OrderResponseDto> acceptOrder(@RequestParam("orderId") UUID orderId) {
        Order updated = orderService.acceptOrder(orderId);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @PostMapping("/complete")
    public ResponseEntity<OrderResponseDto> completeOrder(@RequestParam("orderId") UUID orderId) {
        Order updated = orderService.completeOrder(orderId);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @PostMapping("/extendTime")
    public ResponseEntity<OrderResponseDto> extendTime(@RequestParam("orderId") UUID orderId,
                                                       @RequestParam int minutes) {
        Order updated = orderService.extendOrderTime(orderId, minutes);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @PostMapping("/reject")
    public ResponseEntity<Order> rejectOrder(@RequestParam("orderId") UUID orderId) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, OrderStatus.CANCELED);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Order> cancelOrder(@RequestParam("orderId") UUID orderId) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, OrderStatus.CANCELED);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @GetMapping("/detail")
    public ResponseEntity<OrderResponseDto> getOrderDetail(@RequestParam("orderId") UUID orderId) {
        Order order = orderService.findOrder(orderId); // 또는 Optional or DTO 변환
        OrderResponseDto dto = convertToDto(order); // 또는 convertToDto(order)
        return ResponseEntity.ok(dto);
    }
    
    // 주문 전체 목록, 특정 상태 목록 등 조회용 API
    @GetMapping("/list")
    public ResponseEntity<List<OrderResponseDto>> getOrderList() {
        List<Order> orders = orderService.findAllByOwner(); // 실제 로직
        List<OrderResponseDto> dtos = orders.stream()
            .map(this::convertToDto) // 혹은 convertToDto(order)
            .toList();
        return ResponseEntity.ok(dtos);
    }
    
    private OrderResponseDto convertToDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setStoreRequest(order.getStoreRequest());
        dto.setDeliveryRequest(order.getDeliveryRequest());
        dto.setOrderStatus(order.getOrderStatus().name());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setOrderTime(order.getOrderTime());

        List<OrderResponseDto.OrderDetailDto> detailDtos = order.getOrderDetails().stream().map(detail -> {
            OrderResponseDto.OrderDetailDto d = new OrderResponseDto.OrderDetailDto();

            if (detail.getMenu() != null) {
                d.setMenuName(detail.getMenu().getMenuName());
            } else {
                d.setMenuName("알 수 없음");
            }

            d.setQuantity(detail.getQuantity());
            d.setPrice(detail.getPrice());
            d.setOptionNames(detail.getOptionNames() != null ? detail.getOptionNames() : List.of());

            return d;
        }).toList();

        dto.setOrderDetails(detailDtos);
        return dto;
    }
}