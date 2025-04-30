package com.soldesk6F.ondal.useract.cart.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class CartAddRequestDto {
	private UUID menuId;
	private UUID storeId;
	private int quantity;
	private List<String> options;
}