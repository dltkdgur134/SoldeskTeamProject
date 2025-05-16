package com.soldesk6F.ondal.useract.order.dto;


import java.util.List;
import java.util.UUID;

import com.soldesk6F.ondal.useract.order.entity.Order;

import lombok.Data;

@Data
public class OrderHistoryDto {
	private UUID userId;
    private UUID orderId;
    private String storeName;
    private String storeImageUrl;
    private String orderStatus;
    private List<String> menuItems;
    private String orderDate;      // ISO 문자열 또는 포맷팅된 날짜

    public static OrderHistoryDto from(Order order) {
        OrderHistoryDto dto = new OrderHistoryDto();
        dto.setOrderId(order.getOrderId());
        dto.setStoreName(order.getStore().getStoreName());
        dto.setStoreImageUrl(order.getStore().getBrandImg());
        dto.setOrderStatus(order.getOrderToOwner().name());
        //dto.setOrderStatus(order.getOrderToOwner().getDescription().toString());
        dto.setOrderDate(order.getOrderTime().toString());
        dto.setMenuItems(order.getOrderDetails().stream()
            .map(d -> d.getMenu().getMenuName())
            .toList());
        return dto;
    }
}