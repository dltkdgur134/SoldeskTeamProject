package com.soldesk6F.ondal.useract.cart.repository;

import com.soldesk6F.ondal.useract.cart.entity.CartItemOption;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CartItemOptionRepository extends JpaRepository<CartItemOption, UUID> {
	
	@Modifying
	@Transactional
	@Query("DELETE FROM CartItemOption o WHERE o.cartItem = :cartItem")
	void deleteByCartItem(@Param("cartItem") CartItems cartItem);
	
	List<CartItemOption> findByCartItem(CartItems cartItem);
}

