package com.soldesk6F.ondal.useract.order.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToOwner;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToRider;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToUser;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponseDto {
	private UUID userUuid;
    private UUID orderId;
    private Integer orderNumber;
    private String deliveryAddress;
    private String storeRequest;
    private String deliveryRequest;
    private int totalPrice;
    private int deliveryFee;
    private LocalDateTime orderTime;
    private LocalDateTime cookingStartTime;
    private LocalTime expectCookingTime;

    private OrderToUser orderToUser;
    private OrderToOwner orderToOwner;
    private OrderToRider orderToRider;
    
    private List<OrderDetailDto> orderDetails;

    // ✅ 추가 필드 (프론트에서 팝업 요약용)
    private String menuNameList;
    private int totalCount;
    private String contactNumber;

    @Getter
    @Builder
    public static class OrderDetailDto {
        private UUID orderDetailId;
        private String menuName;
        private int quantity;
        private int price;
        private List<String> optionNames;
        private List<Integer> optionPrices;
    }

    public static OrderResponseDto from(Order order) {
        List<OrderDetailDto> detailDtos = order.getOrderDetails().stream()
            .map(detail -> OrderDetailDto.builder()
                .orderDetailId(detail.getOrderDetailId())
                .menuName(detail.getMenu().getMenuName())
                .quantity(detail.getQuantity())
                .price(detail.getPrice())
                .optionNames(detail.getOptionNames())
                .optionPrices(detail.getOptionPrices())
                .build())
            .collect(Collectors.toList());

        // ✅ 메뉴 요약 및 수량 합계
        String menuSummary = "";
        int totalCount = 0;
        if (!order.getOrderDetails().isEmpty()) {
            menuSummary = order.getOrderDetails().get(0).getMenu().getMenuName();
            int more = order.getOrderDetails().size() - 1;
            if (more > 0) menuSummary += " 외 " + more + "개";
            totalCount = order.getOrderDetails().stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();
        }

        return OrderResponseDto.builder()
        	.userUuid(order.getUser() != null ? order.getUser().getUserUuid() : null)
            .orderId(order.getOrderId())
            .orderToUser(order.getOrderToUser())
            .orderToOwner(order.getOrderToOwner())
            .orderToRider(order.getOrderToRider())
            .orderToUser(order.getOrderToUser())
            .deliveryAddress(order.getDeliveryAddress())
            .storeRequest(order.getStoreRequest())
            .deliveryRequest(order.getDeliveryRequest())
            .totalPrice(order.getTotalPrice())
            .orderTime(order.getOrderTime())
            .expectCookingTime(order.getExpectCookingTime())
            .cookingStartTime(order.getCookingStartTime())
            .orderDetails(detailDtos)
            .deliveryFee(order.getDeliveryFee())
            // ✅ 신규 주문 알림용 필드
            .menuNameList(menuSummary)
            .totalCount(totalCount)
            .contactNumber(order.getUser() != null ? order.getUser().getUserPhone() : "비회원")
            .build();
    }
}
