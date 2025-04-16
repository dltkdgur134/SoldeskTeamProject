package com.soldesk6F.ondal.owner.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.soldesk6F.ondal.useract.order.dto.OrderRequestDto;
import com.soldesk6F.ondal.useract.order.dto.OrderRequestDto.OrderDetailDto;
import com.soldesk6F.ondal.useract.order.dto.OrderResponseDto;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderStatus;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.menu.repository.MenuRepository;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Order saveOrder(OrderRequestDto requestDto) {
        Store store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(() -> new RuntimeException("가게를 찾을 수 없습니다. storeId=" + requestDto.getStoreId()));

        Order order = Order.builder()
                .store(store)
                .deliveryAddress(requestDto.getDeliveryAddress())
                .storeRequest(requestDto.getStoreRequest())
                .deliveryRequest(requestDto.getDeliveryRequest())
                .orderAdditional1(requestDto.getOrderAdditional1())
                .orderAdditional2(requestDto.getOrderAdditional2())
                .orderStatus(OrderStatus.PENDING)
                .build();

        if (requestDto.getOrderDetails() != null) {
            for (OrderDetailDto detailDto : requestDto.getOrderDetails()) {
                Menu menu = menuRepository.findById(detailDto.getMenuId())
                        .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다."));
                OrderDetail orderDetail = new OrderDetail(order, menu, detailDto.getQuantity(),
                        detailDto.getPrice(), detailDto.getOptionNames(), detailDto.getOptionPrices());
                order.addOrderDetail(orderDetail);
            }
        }

        return orderRepository.save(order);
    }
    
    @Transactional
    public Order acceptOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + orderId));

        // 상태 변경
        order.setOrderStatus(Order.OrderStatus.CONFIRMED);

        // 필요한 경우 예상 완료 시간도 설정 (예: 현재 시간 + 20분)
        // order.setExpectedCompletionTime(LocalDateTime.now().plusMinutes(20));

        return orderRepository.save(order);
    }


    // 조리 완료
    @Transactional
    public Order completeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setOrderStatus(OrderStatus.COMPLETED);
        return orderRepository.save(order);
    }

    // 시간 추가 (임시: 별도 필드 없으므로 로그만 출력)
    @Transactional
    public Order extendOrderTime(UUID orderId, int minutes) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        // 실사용 시 deliveryEstimatedTime 필드 등을 추가해 갱신
        System.out.println("주문 " + orderId + "에 시간 " + minutes + "분 추가");
        return order;
    }
    
    @Transactional
    public Order updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setOrderStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        if (savedOrder.getUser() != null) {
            messagingTemplate.convertAndSend("/topic/user/" + savedOrder.getUser().getUserUuidAsString(),
                    convertToDto(savedOrder));
        }

        if (savedOrder.getRider() != null) {
            messagingTemplate.convertAndSend("/topic/rider/" + savedOrder.getRider().getRiderUuidAsString(),
                    convertToDto(savedOrder));
        }

        return savedOrder;
    }

    public Order findOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    @Transactional(readOnly = true)
    public List<Order> findAllByOwner() {
    	return orderRepository.findAll();
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

        dto.setOrderDetails(order.getOrderDetails().stream().map(detail -> {
            OrderResponseDto.OrderDetailDto detailDto = new OrderResponseDto.OrderDetailDto();
            detailDto.setMenuName(detail.getMenu().getMenuName());
            detailDto.setQuantity(detail.getQuantity());
            detailDto.setPrice(detail.getPrice());
            detailDto.setOptionNames(detail.getOptionNames());
            return detailDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}