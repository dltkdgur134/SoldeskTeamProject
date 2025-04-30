package com.soldesk6F.ondal.useract.cart.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
	
	private final CartRepository cartRepository;
	
	public Cart getCartByUser(User user) {
		Optional<Cart> existingCart = cartRepository.findByUser(user);
		if (existingCart.isPresent()) {
			return existingCart.get();
		}

		Cart newCart = Cart.builder()
			.user(user)
			.store(null)
			.build();

		return cartRepository.save(newCart);
	}
	
	public Cart getCartByUserAndStore(User user, Store incomingStore) {
		Optional<Cart> existingCartOpt = cartRepository.findByUser(user);

		if (existingCartOpt.isPresent()) {
			Cart existingCart = existingCartOpt.get();

			// 🔥 다른 가게라면 장바구니 초기화
			if (existingCart.getStore() != null && !existingCart.getStore().getStoreId().equals(incomingStore.getStoreId())) {
				existingCart.getCartItems().clear(); // 연결된 아이템 삭제
				existingCart.setStore(incomingStore);
				return cartRepository.save(existingCart);
			}

			// 장바구니에 store 없던 경우
			if (existingCart.getStore() == null) {
				existingCart.setStore(incomingStore);
				return cartRepository.save(existingCart);
			}

			return existingCart;
		}

		// 처음 생성
		Cart newCart = Cart.builder()
			.user(user)
			.store(incomingStore)
			.build();

		return cartRepository.save(newCart);
	}
	
}

