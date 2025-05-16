package com.soldesk6F.ondal.useract.cart.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartUpdateRequestDto {
	private UUID cartItemUuid;
	private int quantity;
}
