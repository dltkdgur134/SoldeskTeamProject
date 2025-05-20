package com.soldesk6F.ondal.useract.order.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
	
	
}
