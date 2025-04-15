package com.soldesk6F.ondal.owner.order.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.soldesk6F.ondal.owner.order.OrderService;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/owner/order")
@RequiredArgsConstructor
public class OwnerOrderController {

    private final OrderService OrderService;

    @PostMapping("/accept")
    public ResponseEntity<Order> acceptOrder(@RequestParam UUID orderId) {
        Order updatedOrder = OrderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/reject")
    public ResponseEntity<Order> rejectOrder(@RequestParam UUID orderId) {
        Order updatedOrder = OrderService.updateOrderStatus(orderId, OrderStatus.CANCELED);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/complete")
    public ResponseEntity<Order> completeOrder(@RequestParam UUID orderId) {
        Order updatedOrder = OrderService.updateOrderStatus(orderId, OrderStatus.COMPLETED);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Order> cancelOrder(@RequestParam UUID orderId) {
        Order updatedOrder = OrderService.updateOrderStatus(orderId, OrderStatus.CANCELED);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @GetMapping("/detail")
    public Order getOrderDetail(@RequestParam UUID orderId) {
        // DB에서 orderId로 조회 후 JSON 반환
        return OrderService.findOrder(orderId);
    }
    
    // 주문 전체 목록, 특정 상태 목록 등 조회용 API
    @GetMapping("/list")
    public ResponseEntity<List<Order>> getOrderList() {
        List<Order> orders = OrderService.findAllByOwner(); 
        return ResponseEntity.ok(orders);
    }
}