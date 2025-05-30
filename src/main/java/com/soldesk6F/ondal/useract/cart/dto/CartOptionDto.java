package com.soldesk6F.ondal.useract.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartOptionDto {
	private String groupName;
	private String name;
	private int price;
	private boolean selected;

	public CartOptionDto(String groupName, String name, int price) {
		this.groupName = groupName;
		this.name = name;
		this.price = price;
		this.selected = false; // 기본값
	}
	
}


