package com.soldesk6F.ondal.useract.cart.repository;

import com.soldesk6F.ondal.useract.cart.entity.CartItems;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItems, UUID> {

	
}

