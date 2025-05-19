package com.soldesk6F.ondal.useract.order.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
import com.soldesk6F.ondal.useract.payment.entity.Payment;

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
	private int deliveryFee;
	private String paymentMethod;
	private int menuTotalPrice;
	private String phoneNum;
	private String deliveryAddress;
	
	
	public static OrderInfoDetailDto from(Order order) {
		OrderInfoDetailDto dto = new OrderInfoDetailDto();
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
		
		dto.setPhoneNum(order.getUser().getUserPhone());
		dto.setDeliveryAddress(order.getDeliveryAddress());
		return dto;
	}
	
	
	
}
