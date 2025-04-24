package com.soldesk6F.ondal.owner.order;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.menu.repository.MenuRepository;
import com.soldesk6F.ondal.owner.order.dto.OrderLiveDto;
import com.soldesk6F.ondal.owner.order.dto.StatusTimeline;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.order.dto.OrderHistoryDto;
import com.soldesk6F.ondal.useract.order.dto.OrderRequestDto;
import com.soldesk6F.ondal.useract.order.dto.OrderRequestDto.OrderDetailDto;
import com.soldesk6F.ondal.useract.order.dto.OrderResponseDto;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToOwner;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToRider;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
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
    private final UserRepository userRepository;
    

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
                .orderToOwner(OrderToOwner.PENDING)
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
    
    public Order acceptOrder(UUID orderId, int completionTime) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("주문 없음"));

        order.setOrderToOwner(OrderToOwner.CONFIRMED);
        order.setExpectCookingTime(LocalTime.of(0, 0).plusMinutes(completionTime));
        order.setCookingStartTime(LocalDateTime.now());

        return orderRepository.save(order);
    }

    public Order extendCookingTime(UUID orderId, int addMinutes) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("주문 없음"));

        LocalTime current = order.getExpectCookingTime();
        if (current == null) {
            current = LocalTime.of(0, 0); // 기본값: 00:00
        }

        LocalTime updated = current.plusMinutes(addMinutes);
        order.setExpectCookingTime(updated);

        return orderRepository.save(order);
    }

    // 조리 완료
    @Transactional
    public Order completeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setOrderToOwner(OrderToOwner.IN_DELIVERY);
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
    public Order updateOrderStatus(UUID orderId, OrderToOwner orderToOwner) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setOrderToOwner(orderToOwner);
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
    
    public List<Order> getOrdersByStore(UUID storeId) {
        return orderRepository.findByStore_StoreId(storeId);
    }
    
    @Transactional(readOnly = true)
    public List<OrderHistoryDto> getOrderHistoryByUser(String userId) {
        var user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + userId));
        var orders = orderRepository.findByUser(user);
        return orders.stream()
                     .map(this::toHistoryDto)
                     .collect(Collectors.toList());
    }

    private OrderHistoryDto toHistoryDto(Order order) {
        var dto = new OrderHistoryDto();
        dto.setOrderId(order.getOrderId());
        dto.setStoreName(order.getStore().getStoreName());
        dto.setStoreImageUrl(order.getStore().getBrandImg());
        dto.setOrderStatus(order.getOrderToOwner().name());
        dto.setOrderDate(order.getOrderTime().toString());
        var menuNames = order.getOrderDetails().stream()
                             .map(d -> d.getMenu().getMenuName())
                             .collect(Collectors.toList());
        dto.setMenuItems(menuNames);
        return dto;
    }
    private OrderResponseDto convertToDto(Order order) {
        List<OrderResponseDto.OrderDetailDto> detailDtos = order.getOrderDetails().stream()
            .map(detail -> OrderResponseDto.OrderDetailDto.builder()
                .menuName(detail.getMenu().getMenuName())
                .quantity(detail.getQuantity())
                .price(detail.getPrice())
                .optionNames(detail.getOptionNames())
                .build())
            .collect(Collectors.toList());

        return OrderResponseDto.builder()
            .orderId(order.getOrderId())
            .deliveryAddress(order.getDeliveryAddress())
            .storeRequest(order.getStoreRequest())
            .deliveryRequest(order.getDeliveryRequest())
            .orderToOwner(order.getOrderToOwner()) // .name() 필요 없음 (enum 그대로 DTO에 선언되어 있으면)
            .totalPrice(order.getTotalPrice())
            .orderTime(order.getOrderTime())
            .expectCookingTime(order.getExpectCookingTime()) // 이거도 있으면 넣어줘
            .orderDetails(detailDtos)
            .build();
    }

    @Transactional(readOnly = true)
    public OrderToRider getOrderToRider(String orderId) {
        UUID uuid = UUID.fromString(orderId);
        var order = orderRepository.findById(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Invalid orderId: " + orderId));
        return order.getOrderToRider();
    }

    @Transactional(readOnly = true)
    public OrderHistoryDto getOrderHistoryDto(String orderId) {
    	UUID uuid = UUID.fromString(orderId);
        Order order = orderRepository.findById(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Invalid orderId"));
        // 간단히 toDto 매퍼 호출
        return OrderHistoryDto.from(order);
    }

    @Transactional(readOnly = true)
    public OrderLiveDto getOrderLiveDto(String orderId) {
        UUID uuid = UUID.fromString(orderId);
        var order = orderRepository.findById(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Invalid orderId: " + orderId));

        var dto = new OrderLiveDto();
        dto.setOrderId(order.getOrderId().toString());
        dto.setOrderStatus(order.getOrderToRider());

        var timeline = new ArrayList<StatusTimeline>();
        // 시간 필드들이 있다고 가정
        timeline.add(new StatusTimeline("PENDING",             order.getOrderTime()));
        timeline.add(new StatusTimeline("CONFIRMED",           order.getCookingStartTime()));
        timeline.add(new StatusTimeline("COOKING_COMPLETED",   order.getCookingEndTime()));
        timeline.add(new StatusTimeline("IN_DELIVERY",         order.getDeliveryStartTime()));
        timeline.add(new StatusTimeline("COMPLETED",           order.getDeliveryCompleteTime()));
        dto.setTimeline(timeline);

        // 2) 가게 위치 (라이더 대신)
        Store store = order.getStore();
        dto.setLat(store.getStoreLatitude());   // 또는 store.getHubAddressLatitude()
        dto.setLng(store.getStoreLongitude());  // 또는 store.getHubAddressLongitude()

        return dto;
    }
}