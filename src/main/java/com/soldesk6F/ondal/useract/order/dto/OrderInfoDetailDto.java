package com.soldesk6F.ondal.useract.order.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;

import lombok.Data;

@Data
public class OrderInfoDetailDto {
	private UUID orderId;
	private UUID StoreId;
	private String storeName;
	private String storeImageUrl;
	private String orderStatus;
	private LocalDateTime orderDate;
	private LinkedList<HashMap<String, Object>> menuItems;
	private List<String> optionNames;
	private List<Integer> optionPrices;
	private int totalPrice;
	
	public static OrderInfoDetailDto from(Order order) {
		OrderInfoDetailDto dto = new OrderInfoDetailDto();
		dto.setOrderId(order.getOrderId());
		dto.setStoreId(order.getStore().getStoreId());
		dto.setStoreName(order.getStore().getStoreName());
		dto.setStoreImageUrl(order.getStore().getBrandImg());
		dto.setOrderStatus(order.getOrderToOwner().getDescription().toString());
		dto.setOrderDate(order.getOrderTime());
		LinkedList<HashMap<String, Object>> menuItems = new LinkedList<HashMap<String, Object>>();
		for (OrderDetail orderDetails : order.getOrderDetails()) {
			HashMap<String ,Object> menuDetails = new HashMap<String ,Object>();
			String menuName = orderDetails.getMenu().getMenuName();
			int price = orderDetails.getPrice();
			int quantity = orderDetails.getQuantity();
			menuDetails.put("menuName", menuName);
			menuDetails.put("price", price);
			menuDetails.put("quantity", quantity);
			menuItems.add(menuDetails);
		}
		dto.setMenuItems(menuItems);
		dto.setTotalPrice(order.getTotalPrice());
		return dto;
	}
	
	
	
}
