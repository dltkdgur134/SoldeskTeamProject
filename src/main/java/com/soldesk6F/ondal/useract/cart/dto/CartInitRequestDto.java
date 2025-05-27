package com.soldesk6F.ondal.useract.cart.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CartInitRequestDto {

	private UUID userUUID;
	private List<CartAddRequestDto> items; // localStorage에 있는 cart.items 내용

}


