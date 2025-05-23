package com.soldesk6F.ondal.useract.cart.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.menu.service.MenuService;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.cart.dto.CartAddRequestDto;
import com.soldesk6F.ondal.useract.cart.dto.CartOptionDto;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.entity.CartItemOption;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;
import com.soldesk6F.ondal.useract.cart.entity.CartStatus;
import com.soldesk6F.ondal.useract.cart.repository.CartItemsRepository;
import com.soldesk6F.ondal.useract.cart.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
	
	private final MenuService menuService;
	private final CartRepository cartRepository;
	private final CartItemsRepository cartItemsRepository;
	
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
	
	public int getUpdatedTotal(UUID cartItemUuid) {
		CartItems item = cartItemsRepository.findById(cartItemUuid)
			.orElseThrow(() -> new IllegalArgumentException("CartItem not found"));
		
		int menuPrice = item.getMenu().getPrice();
		int optionTotal = item.getCartItemOptions().stream()
			.mapToInt(CartItemOption::getOptionPrice)
			.sum();

		return (menuPrice + optionTotal) * item.getQuantity();
	}
	
	public int getCartTotalPriceForUser(User user) {
		Cart cart = cartRepository.findByUser(user)
			.orElseThrow(() -> new IllegalArgumentException("Cart not found"));

		return cart.getCartItems().stream()
			.mapToInt(item -> {
				int menuPrice = item.getMenu().getPrice();
				int optionTotal = item.getCartItemOptions().stream()
					.mapToInt(CartItemOption::getOptionPrice)
					.sum();
				return (menuPrice + optionTotal) * item.getQuantity();
			})
			.sum();
	}
	
	public Cart findById(UUID cartId) {
		return cartRepository.findById(cartId)
			.orElseThrow(() -> new IllegalArgumentException("해당 장바구니가 존재하지 않습니다: " + cartId));
	}
	
	@Transactional
	public Cart createCart(User user, Store store, List<CartAddRequestDto> itemsDto) {
		// 기존 Cart가 있는지 확인
		Optional<Cart> optionalCart = cartRepository.findByUserAndStore(user, store);

		Cart cart = optionalCart.orElseGet(() -> {
			return Cart.builder()
				.user(user)
				.store(store)
				.status(CartStatus.PENDING)
				.build();
		});

		// 기존 항목 삭제 (필요 시)
		cart.getCartItems().clear(); // 이 줄이 중요함 (중복 방지)
		cartItemsRepository.deleteAllByCart(cart); // DB에서도 삭제

		// 새 항목 추가
		for (CartAddRequestDto itemDto : itemsDto) {
			Menu menu = menuService.findById(itemDto.getMenuId());
			CartItems item = new CartItems(cart, menu, itemDto.getQuantity(), new ArrayList<>());

			for (CartOptionDto opt : itemDto.getOptions()) {
				if (!opt.isSelected()) continue;
				CartItemOption option = new CartItemOption(
					item, opt.getGroupName(), opt.getName(), opt.getPrice());
				item.getCartItemOptions().add(option);
			}
			cart.getCartItems().add(item);
		}

		return cart;
	}
	
}

