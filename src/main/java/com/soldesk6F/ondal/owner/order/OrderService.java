package com.soldesk6F.ondal.owner.order;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.functions.DateFunctions;
import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.menu.repository.MenuRepository;
import com.soldesk6F.ondal.owner.order.dto.OrderLiveDto;
import com.soldesk6F.ondal.owner.order.dto.StatusTimeline;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.order.dto.OrderHistoryDto;
import com.soldesk6F.ondal.useract.order.dto.OrderInfoDetailDto;
import com.soldesk6F.ondal.useract.order.dto.OrderRequestDto;
import com.soldesk6F.ondal.useract.order.dto.OrderRequestDto.OrderDetailDto;
import com.soldesk6F.ondal.useract.order.dto.OrderResponseDto;
import com.soldesk6F.ondal.useract.order.dto.TestOrderRequestDto;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToOwner;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToRider;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;
import com.soldesk6F.ondal.useract.payment.entity.Payment;
import com.soldesk6F.ondal.useract.payment.repository.PaymentRepository;
import com.soldesk6F.ondal.useract.regAddress.entity.RegAddress;
import com.soldesk6F.ondal.useract.regAddress.repository.RegAddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final DateFunctions dateFunctions;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final RegAddressRepository regAddressRepository;
    private final PaymentRepository paymentRepository;
    

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
        	String destination = "/topic/user/" + savedOrder.getUser().getUserUuidAsString();
            OrderResponseDto orderDto = convertToDto(savedOrder);
        	
            System.out.println("발행 경로: " + destination);
            System.out.println("발행 메시지: " + orderDto);
            
            messagingTemplate.convertAndSend(destination, orderDto);
        }

        if (savedOrder.getRider() != null) {
            String destination = "/topic/rider/" + savedOrder.getRider().getRiderUuidAsString();
            OrderResponseDto orderDto = convertToDto(savedOrder);
            System.out.println("발행 경로: " + destination);
            System.out.println("발행 메시지: " + orderDto);
            messagingTemplate.convertAndSend(destination, orderDto);
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
    
//    @Transactional(readOnly = true)
//    public List<OrderHistoryDto> getOrderHistoryByUser(String userId) {
//        var user = userRepository.findByUserId(userId)
//            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + userId));
//        var orders = orderRepository.findByUser(user);
//        return orders.stream()
//                     .map(this::toHistoryDto)
//                     .collect(Collectors.toList());
//    }
    
    @Transactional(readOnly = true)
    public List<OrderHistoryDto> getOrderHistoryByUser(String userId) {
        var user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + userId));
        var orders = orderRepository.findByUser(user);
        List<OrderHistoryDto> orderHistoryDto = orders.stream()
        		.map(this::toHistoryDto)
        		.collect(Collectors.toList());
        orderHistoryDto.forEach(dto -> {
        	long daysLeft = dateFunctions.getDaysLeftForReview(dto.getOrderDate());
        	dto.setDaysLeftForReview(daysLeft);
        });
        orderHistoryDto.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
        return orderHistoryDto;
    }
    

    private OrderHistoryDto toHistoryDto(Order order) {
        var dto = new OrderHistoryDto();
        dto.setOrderId(order.getOrderId());
        dto.setStoreId(order.getStore().getStoreId());
        dto.setStoreName(order.getStore().getStoreName());
        dto.setStoreImageUrl(order.getStore().getBrandImg());
        dto.setOrderStatus(order.getOrderToOwner().name());
        //dto.setOrderStatus(order.getOrderToOwner().getDescription().toString());
        //dto.setOrderDate(order.getOrderTime().toString());
        dto.setOrderDate(order.getOrderTime());
        dto.setTotalPrice(order.getTotalPrice());
//        var menuNames = order.getOrderDetails().stream()
//                             .map(d -> d.getMenu().getMenuName())
//                             .collect(Collectors.toList());
//        dto.setMenuItems(menuNames);
        var menuItems = new HashMap<String, Integer>();
        for (int i = 0; i < order.getOrderDetails().size(); i++) {
        	menuItems.put(order.getOrderDetails().get(i).getMenu().getMenuName(), 
        			order.getOrderDetails().get(i).getQuantity());
        }
        dto.setMenuItems(menuItems);
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

//    @Transactional(readOnly = true)
//    public OrderHistoryDto getOrderHistoryDto(String orderId) {
//    	UUID uuid = UUID.fromString(orderId);
//        Order order = orderRepository.findById(uuid)
//            .orElseThrow(() -> new IllegalArgumentException("Invalid orderId"));
//        // 간단히 toDto 매퍼 호출
//        return OrderHistoryDto.from(order);
//    }
    
    @Transactional(readOnly = true)
    public OrderInfoDetailDto getOrderInfoDetailDto(String orderId) {
    	UUID OrderUuid = UUID.fromString(orderId);
        Order order = orderRepository.findById(OrderUuid)
            .orElseThrow(() -> new IllegalArgumentException("Invalid orderId"));
        // 간단히 toDto 매퍼 호출
        OrderInfoDetailDto orderInfoDetailDto = toOrderInfoDetailsDto(order);
        return orderInfoDetailDto;
    }
    
    private OrderInfoDetailDto toOrderInfoDetailsDto(Order order) {
        var dto = new OrderInfoDetailDto();
        dto.setOrderId(order.getOrderId());
		dto.setStoreId(order.getStore().getStoreId());
		dto.setStoreName(order.getStore().getStoreName());
		dto.setStoreImageUrl(order.getStore().getBrandImg());
		dto.setOrderStatus(order.getOrderToOwner().getDescription().toString());
		dto.setOrderDate(order.getOrderTime());
		LinkedList<HashMap<String, Object>> menuItems = new LinkedList<HashMap<String, Object>>();
		int menuTotalPrice = 0;
		for (OrderDetail orderDetails : order.getOrderDetails()) {
			HashMap<String ,Object> menuDetails = new HashMap<String ,Object>();
			menuDetails.put("menuName", orderDetails.getMenu().getMenuName());
			menuDetails.put("menuPrice", orderDetails.getMenu().getPrice());
			menuDetails.put("price", orderDetails.getPrice());
			menuDetails.put("quantity", orderDetails.getQuantity());
			
			// 옵션 이름 , 가격을 HashMap에 담아서 해당하는 메뉴에 추가
			HashMap<String, Integer> options = new HashMap<String, Integer>();
			
			for (int i = 0; i < orderDetails.getOptionNames().size(); i++) {
				options.put(orderDetails.getOptionNames().get(i), orderDetails.getOptionPrices().get(i));
			}
			menuDetails.put("options", options);
			
			menuItems.add(menuDetails);
			menuTotalPrice += orderDetails.getPrice();
		}
		dto.setMenuItems(menuItems);
		dto.setTotalPrice(order.getTotalPrice());
		dto.setDeliveryFee(order.getDeliveryFee());
		dto.setMenuTotalPrice(menuTotalPrice);
		
		Optional<Payment> payment = paymentRepository.findByOrder(order);
		if (payment.isEmpty() || payment.get() == null) {
			dto.setPaymentMethod("정보 조회 불가");
		} else {
			dto.setPaymentMethod(payment.get().getPaymentMethod().getDescription());
		}
		dto.setPhoneNum(order.getUser().getUserPhone());
		dto.setDeliveryAddress(order.getDeliveryAddress());
		return dto;
    }
    
    
    @Transactional(readOnly = true)
    public OrderLiveDto getOrderLiveDto(String orderId) {
        UUID uuid = UUID.fromString(orderId);
        var order = orderRepository.findById(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Invalid orderId: " + orderId));

        var dto = new OrderLiveDto();
        dto.setOrderId(order.getOrderId().toString());
        dto.setOrderStatus(order.getOrderToRider());
        // 추가
        dto.setExpectCookingTime(order.getExpectCookingTime());

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
    
    @Transactional
    public void createTestOrder(TestOrderRequestDto dto, UUID userId) {
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid store ID"));
        Menu menu = menuRepository.findById(dto.getMenuId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu ID"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        RegAddress address = regAddressRepository.findByUserAndIsUserSelectedAddressTrue(user)
        		.orElseThrow(() -> new IllegalStateException("사용자가 선택한 주소가 없습니다."));

        Order order = new Order();
        order.setUser(user);
        order.setStore(store);
        order.setOrderTime(LocalDateTime.now());
        order.setOrderToOwner(OrderToOwner.PENDING);
        order.setOrderToRider(OrderToRider.PENDING);
        order.setDeliveryAddress(address.getAddress() + " " + address.getDetailAddress()); // ✅ 전체 주소
        order.setDeliveryAddressLatitude(address.getUserAddressLatitude());  // ✅ 위도
        order.setDeliveryAddressLongitude(address.getUserAddressLongitude());  // ✅ 경도
        order.calculateDeliveryFee();                        // 거리기반 배달료 설정

        OrderDetail detail = new OrderDetail();
        detail.setMenu(menu);
        detail.setQuantity(dto.getQuantity());
        detail.setOptionNames(List.of());  // 테스트용
        detail.setOptionPrices(List.of());
        detail.setOrder(order); // 이거는 addOrderDetail 안에서 하면 생략 가능
        detail.setPrice(menu.getPrice());

        order.addOrderDetail(detail); // 핵심! totalPrice 자동 계산됨

        orderRepository.save(order);
    }

    
}