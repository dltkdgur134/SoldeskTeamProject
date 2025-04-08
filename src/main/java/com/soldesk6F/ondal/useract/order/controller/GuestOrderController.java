package com.soldesk6F.ondal.useract.order.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.menu.repository.MenuRepository;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.useract.order.dto.GuestOrderRequestDto;
import com.soldesk6F.ondal.useract.order.dto.GuestOrderResponseDto;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
import com.soldesk6F.ondal.useract.order.repository.OrderDetailRepository;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/guest/orders")
@RequiredArgsConstructor
public class GuestOrderController {

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final MenuRepository menuRepository;

    @PostMapping
    public ResponseEntity<?> placeGuestOrder(@RequestBody GuestOrderRequestDto dto,
                                             HttpServletRequest request) {
        String guestId = (String) request.getAttribute("guest_id");
        if (guestId == null) {
            return ResponseEntity.badRequest().body("게스트 ID를 찾을 수 없습니다.");
        }

        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        Order order = Order.builder()
                .guestId(guestId)
                .store(store)
                .deliveryAddress(dto.getDeliveryAddress())
                .storeRequest(dto.getStoreRequest())
                .deliveryRequest(dto.getDeliveryRequest())
                .orderAdditional1(dto.getOrderAdditional1())
                .orderAdditional2(dto.getOrderAdditional2())
                .orderStatus(Order.OrderStatus.PENDING)
                .totalPrice(0)
                .build();

        for (GuestOrderRequestDto.OrderDetailDto detailDto : dto.getOrderDetails()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            Menu menu = menuRepository.findById(detailDto.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
            detail.setMenu(menu);
            detail.setQuantity(detailDto.getQuantity());
            detail.setPrice(detailDto.getPrice());
            order.addOrderDetail(detail);
        }

        order.updateTotalPrice();
        orderRepository.save(order);

        return ResponseEntity.ok("주문이 정상적으로 접수되었습니다.");
    }

    @GetMapping
    public ResponseEntity<?> getGuestOrders(HttpServletRequest request) {
        String guestId = (String) request.getAttribute("guest_id");
        if (guestId == null) {
            return ResponseEntity.badRequest().body("게스트 ID를 찾을 수 없습니다.");
        }

        List<Order> orders = orderRepository.findByGuestId(guestId);

        List<GuestOrderResponseDto> response = orders.stream().map(order -> {
            GuestOrderResponseDto dto = new GuestOrderResponseDto();
            dto.setOrderId(order.getOrderId());
            dto.setDeliveryAddress(order.getDeliveryAddress());
            dto.setStoreRequest(order.getStoreRequest());
            dto.setDeliveryRequest(order.getDeliveryRequest());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setStoreName(order.getStore().getStoreName());
            dto.setOrderDate(order.getOrderTime());
            dto.setOrderStatus(order.getOrderStatus().name());

            List<GuestOrderResponseDto.OrderDetailDto> detailDtos = order.getOrderDetails().stream().map(detail -> {
                GuestOrderResponseDto.OrderDetailDto detailDto = new GuestOrderResponseDto.OrderDetailDto();
                detailDto.setMenuName(detail.getMenu().getMenuName());
                detailDto.setQuantity(detail.getQuantity());
                detailDto.setPrice(detail.getPrice());
                detailDto.setSelectedOptions(
                        IntStream.range(0, detail.getOptionNames().size())
                                .mapToObj(i -> {
                                    String name = detail.getOptionNames().get(i);
                                    Integer price = i < detail.getOptionPrices().size() ? detail.getOptionPrices().get(i) : 0;
                                    return name + " (+" + price + "원)";
                                })
                                .toList()
                );
                return detailDto;
            }).toList();

            dto.setOrderDetails(detailDtos);
            return dto;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getGuestOrderDetail(@PathVariable UUID orderId,
                                                 HttpServletRequest request) {
        String guestId = (String) request.getAttribute("guest_id");
        if (guestId == null) {
            return ResponseEntity.badRequest().body("게스트 ID를 찾을 수 없습니다.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!guestId.equals(order.getGuestId())) {
            return ResponseEntity.status(403).body("이 주문에 접근할 수 없습니다.");
        }

        GuestOrderResponseDto dto = new GuestOrderResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setStoreRequest(order.getStoreRequest());
        dto.setDeliveryRequest(order.getDeliveryRequest());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStoreName(order.getStore().getStoreName());
        dto.setOrderDate(order.getOrderTime());
        dto.setOrderStatus(order.getOrderStatus().name());

        List<GuestOrderResponseDto.OrderDetailDto> detailDtos = order.getOrderDetails().stream().map(detail -> {
            GuestOrderResponseDto.OrderDetailDto detailDto = new GuestOrderResponseDto.OrderDetailDto();
            detailDto.setMenuName(detail.getMenu().getMenuName());
            detailDto.setQuantity(detail.getQuantity());
            detailDto.setPrice(detail.getPrice());
            detailDto.setSelectedOptions(
                    IntStream.range(0, detail.getOptionNames().size())
                            .mapToObj(i -> {
                                String name = detail.getOptionNames().get(i);
                                Integer price = i < detail.getOptionPrices().size() ? detail.getOptionPrices().get(i) : 0;
                                return name + " (+" + price + "원)";
                            })
                            .toList()
            );
            return detailDto;
        }).toList();

        dto.setOrderDetails(detailDtos);

        return ResponseEntity.ok(dto);
    }
}
