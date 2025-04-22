package com.soldesk6F.ondal.useract.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public Order createOrder(Order order) {
        order.calculateDeliveryFee();  // 배달료 계산
        order.updateTotalPrice();  // 총 가격 업데이트
        return orderRepository.save(order);
    }
}
