package com.soldesk6F.ondal.owner.order.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.soldesk6F.ondal.useract.order.entity.Order.OrderToOwner;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToRider;

import lombok.Data;

@Data
public class OrderLiveDto {
//    private String orderId;
//    private OrderToRider orderStatus;
//    private List<StatusTimeline> timeline;
//    private double Lat;
//    private double Lng;
	
	private String orderId;
	private UUID StoreId;
	private String storeName;
	private String storeImageUrl;
	private LocalDateTime orderDate;
	private LinkedList<HashMap<String, Object>> menuItems;
	private List<String> optionNames;
	private List<Integer> optionPrices;
	private int menuTotalPrice;
	private int totalPrice;
	private int deliveryFee;
	private String paymentMethod;
	private String phoneNum;
	private String deliveryAddress;
	private OrderToRider deliveryStatus;
	private OrderToOwner cookingStatus;
	private List<StatusTimeline> timeline;
	private double Lat;
	private double Lng;
    private LocalTime expectCookingTime; 
    private LocalTime expectDeliveryTime;
    private int currentStatus;
    // + getters / setters
}
