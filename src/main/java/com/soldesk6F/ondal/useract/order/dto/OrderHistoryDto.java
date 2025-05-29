package com.soldesk6F.ondal.useract.order.dto;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.soldesk6F.ondal.useract.order.entity.Order.OrderToUser;

import lombok.Data;

@Data
public class OrderHistoryDto {
	private UUID userId;
    private UUID orderId;
    private UUID storeId;
    private String storeName;
    private String storeImageUrl;
    private String orderStatus;
    private OrderToUser orderToUser;
    //private List<String> menuItems;
    private HashMap<String, Integer> menuItems;
    private String menuName;
    private int menuQuantity;
    //private String orderDate;      // ISO 문자열 또는 포맷팅된 날짜
    private LocalDateTime orderDate;
    private Long daysLeftForReview;
    private int totalPrice;
    private boolean reivewWrited;

//    public static OrderHistoryDto from(Order order) {
//        OrderHistoryDto dto = new OrderHistoryDto();
//        dto.setOrderId(order.getOrderId());
//        dto.setStoreId(order.getStore().getStoreId());
//        dto.setStoreName(order.getStore().getStoreName());
//        dto.setStoreImageUrl(order.getStore().getBrandImg());
//        dto.setOrderStatus(order.getOrderToOwner().name());
//        dto.setOrderDate(order.getOrderTime());
//        dto.setMenuItems(order.getOrderDetails().stream()
//            .map(d -> d.getMenu().getMenuName())
//            .toList());
//        return dto;
//    }
}