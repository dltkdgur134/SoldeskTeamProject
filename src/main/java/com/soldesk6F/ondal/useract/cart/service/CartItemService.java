package com.soldesk6F.ondal.useract.cart.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
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
import com.soldesk6F.ondal.useract.cart.repository.CartItemsRepository;
import com.soldesk6F.ondal.useract.cart.repository.CartRepository;
import com.soldesk6F.ondal.useract.cart.dto.CartOptionDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {

	private final CartRepository cartRepository;
	private final CartItemOptionRepository cartItemOptionRepository;
	private final CartItemsRepository cartItemsRepository;

	public void addItemToCart(Cart cart, Menu menu, Store store, int quantity, List<CartOptionDto> selectedOptions) {
		cart.setStore(store);
		
		for (CartItems existingItem : cart.getCartItems()) {
			boolean sameMenu = existingItem.getMenu().getMenuId().equals(menu.getMenuId());
			boolean sameOptions = isSameOptions(existingItem.getCartItemOptions(), selectedOptions);

			if (sameMenu && sameOptions) {
				existingItem.setQuantity(existingItem.getQuantity() + quantity);
				cartItemsRepository.save(existingItem);
				return;
			}
		}

		CartItems cartItem = new CartItems();
		cartItem.setCart(cart);
		cartItem.setMenu(menu);
		cartItem.setQuantity(quantity);
		cartItem.setAddedTime(LocalDateTime.now()); // @CreationTimestamp 대체 가능
		cartItem.setMenuName(menu.getMenuName());
		cartItem.setMenuPrice(menu.getPrice());
		cartItem.setMenuImage(menu.getMenuImg());

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
	
	private boolean isSameOptions(List<CartItemOption> existingOptions, List<CartOptionDto> newOptions) {
		if (existingOptions.size() != newOptions.size()) return false;

		for (CartOptionDto newOpt : newOptions) {
			boolean matched = existingOptions.stream()
				.anyMatch(existingOpt ->
					existingOpt.getGroupName().equals(newOpt.getGroupName()) &&
					existingOpt.getOptionName().equals(newOpt.getName()) &&
					existingOpt.getOptionPrice() == newOpt.getPrice());
			if (!matched) return false;
		}
		return true;
	}

	@Transactional
	public void updateOptions(CartItems cartItem, List<CartOptionDto> options) {
		UUID cartItemId = cartItem.getCartItemsId();
		Cart cart = cartItem.getCart();

		List<CartItems> allItems = cart.getCartItems();

		// 병합 대상 먼저 찾기
		for (CartItems other : allItems) {
			if (other.getCartItemsId().equals(cartItemId)) continue;

			boolean sameMenu =
				other.getMenu().getMenuId().equals(cartItem.getMenu().getMenuId()) &&
				other.getMenuPrice() == cartItem.getMenuPrice() &&
				Objects.equals(other.getMenuImage(), cartItem.getMenuImage());

			boolean sameOptions = isSameOptions(other.getCartItemOptions(), options);

			if (sameMenu && sameOptions) {
				// 병합
				other.setQuantity(other.getQuantity() + cartItem.getQuantity());
				cart.getCartItems().remove(cartItem);
				cartItemsRepository.delete(cartItem);
				return;
			}
		}

		// 병합 대상 없을 경우: 옵션 갱신

		// 기존 옵션 삭제
		cartItemOptionRepository.deleteByCartItem(cartItem);

		// 새 옵션 저장
		List<CartItemOption> newOptions = options.stream()
			.map(dto -> new CartItemOption(cartItem, dto.getGroupName(), dto.getName(), dto.getPrice()))
			.collect(Collectors.toList());
		cartItemOptionRepository.saveAll(newOptions);

		// 옵션 총합 저장
		int totalOptionPrice = newOptions.stream().mapToInt(CartItemOption::getOptionPrice).sum();
		cartItem.setOptionTotalPrice(totalOptionPrice);

		cartItemsRepository.save(cartItem);
	}
}

