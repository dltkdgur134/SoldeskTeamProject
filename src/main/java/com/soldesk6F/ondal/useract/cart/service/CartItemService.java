package com.soldesk6F.ondal.useract.cart.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.entity.CartItemOption;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;
import com.soldesk6F.ondal.useract.cart.repository.CartItemOptionRepository;
import com.soldesk6F.ondal.useract.cart.repository.CartItemRepository;
import com.soldesk6F.ondal.useract.cart.repository.CartRepository;
import com.soldesk6F.ondal.useract.cart.dto.CartOptionDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {

	private final CartRepository cartRepository;
	private final CartItemOptionRepository cartItemOptionRepository;
	private final CartItemRepository cartItemRepository;

	public void addItemToCart(Cart cart, Menu menu, Store store, int quantity, List<CartOptionDto> selectedOptions) {
		cart.setStore(store);

		CartItems cartItem = new CartItems();
		cartItem.setCart(cart);
		cartItem.setMenu(menu);
		cartItem.setQuantity(quantity);
		cartItem.setAddedTime(LocalDateTime.now()); // @CreationTimestamp 대체 가능

		int totalOptionPrice = 0;
		List<CartItemOption> cartItemOptions = new ArrayList<>();

		for (CartOptionDto dto : selectedOptions) {
			CartItemOption option = new CartItemOption();
			option.setCartItem(cartItem);
			option.setGroupName(dto.getGroupName());
			option.setOptionName(dto.getName());
			option.setOptionPrice(dto.getPrice());

			totalOptionPrice += dto.getPrice();
			cartItemOptions.add(option);
		}

		cartItem.setCartItemOptions(cartItemOptions);
		cartItem.setOptionTotalPrice(totalOptionPrice);

		cart.getCartItems().add(cartItem);

		cartRepository.save(cart);
	}

	@Transactional
	public void updateOptions(CartItems cartItem, List<CartOptionDto> options) {
		cartItemOptionRepository.deleteByCartItem(cartItem);


		// 새 옵션 저장
		List<CartItemOption> newOptions = options.stream()
			.map(dto -> new CartItemOption(cartItem, dto.getGroupName(), dto.getName(), dto.getPrice()))
			.collect(Collectors.toList());
		cartItemOptionRepository.saveAll(newOptions);

		// 옵션 총합 가격 저장
		int totalOptionPrice = newOptions.stream().mapToInt(CartItemOption::getOptionPrice).sum();
		cartItem.setOptionTotalPrice(totalOptionPrice);
		cartItemRepository.save(cartItem);
	}
}

