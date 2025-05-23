package com.soldesk6F.ondal.useract.cart.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;

public interface CartItemsRepository extends JpaRepository<CartItems, UUID> {

	List<CartItems>findByCart_CartId(UUID cartId);
	void deleteByCart_cartId(UUID cartId);
	
	@Transactional
	void deleteAllByCart(Cart cart);
	
}
