package com.soldesk6F.ondal.useract.order.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.useract.order.entity.Order.OrderToUser;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

@Service
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public OrderQueryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /** 로그인 사용자의 “진행 중” 주문 ID 목록 */
    public List<UUID> getActiveOrderIds(UUID userUuid) {
        return orderRepository.findActiveOrderIds(
                userUuid,
                ACTIVE_STATUSES);
    }

    /** 진행 중으로 간주하는 상태 Enum 값들 */
//    private static final List<OrderToOwner> ACTIVE_STATUSES = List.of(
//            OrderToOwner.PENDING,
//            OrderToOwner.CONFIRMED,
//            OrderToOwner.IN_DELIVERY
//    );
    private static final List<OrderToUser> ACTIVE_STATUSES = List.of(
    		OrderToUser.PENDING,
    		OrderToUser.CONFIRMED,
    		OrderToUser.COOKING,
    		OrderToUser.DELIVERING
    );
}
