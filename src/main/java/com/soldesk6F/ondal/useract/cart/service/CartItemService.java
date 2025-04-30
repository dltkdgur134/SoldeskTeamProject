package com.soldesk6F.ondal.useract.cart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;
import com.soldesk6F.ondal.useract.cart.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {
	
	private final CartRepository cartRepository;
	
	public void addItemToCart(Cart cart, Menu menu, Store store, int quantity, List<String> selectedOptions) {
		cart.setStore(store);
		
		CartItems cartItem = CartItems.builder()
			.cart(cart)
			.menu(menu)
			.quantity(quantity)
			.selectedOptions(selectedOptions)
			.build();

		cart.getCartItems().add(cartItem);
		
		cartRepository.save(cart);
	}
}

