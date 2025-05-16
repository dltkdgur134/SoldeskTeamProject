package com.soldesk6F.ondal.useract.cart.dto;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemOptionSaveDto {
	private UUID cartItemUuid;
	private List<CartOptionDto> options;
}
