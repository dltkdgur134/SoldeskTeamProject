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
		// 1. Cart 객체 생성 및 저장
		Cart cart = Cart.builder()
			.user(user)
			.store(store)
			.status(CartStatus.PENDING)
			.build();
		cart = cartRepository.save(cart); // ✅ 여기서 먼저 save 해야 JPA가 ID 부여함

		// 2. 기존 항목 제거 (해당 cart에 대한 기존 내용 정리)
		cartItemsRepository.deleteAllByCart(cart);

		// 3. 새 항목 추가
		for (CartAddRequestDto itemDto : itemsDto) {
			Menu menu = menuService.findById(itemDto.getMenuId());
			CartItems item = new CartItems(cart, menu, itemDto.getQuantity(), new ArrayList<>());
			
			item.setMenuName(menu.getMenuName());
			item.setMenuPrice(menu.getPrice());
			item.setMenuImage(menu.getMenuImg());

			for (CartOptionDto opt : itemDto.getOptions()) {
				/* if (!opt.isSelected()) continue; */
				CartItemOption option = new CartItemOption();
				option.setGroupName(opt.getGroupName());
				option.setOptionName(opt.getName());
				option.setOptionPrice(opt.getPrice());
				item.addCartOption(option);
			}
			
			int optionTotal = itemDto.getOptions().stream()
					/* .filter(CartOptionDto::isSelected) */
				.mapToInt(CartOptionDto::getPrice)
				.sum();

			item.setOptionTotalPrice(optionTotal);
			
			cart.getCartItems().add(item);
			cartItemsRepository.save(item); // 명시적으로 저장
		}

		return cart;
	}
	
	public Optional<Cart> findLatestCartByUser(User user) {
		return cartRepository.findByUser(user)
			.filter(cart -> cart.getStatus() == CartStatus.PENDING || cart.getStatus() == CartStatus.CANCELED);
	}

	@Transactional
	public void deleteCart(Cart cart) {
		// 장바구니 항목 먼저 삭제 (연관관계 정리)
		cartItemsRepository.deleteAllByCart(cart);
		cartRepository.delete(cart);
	}

	public Cart getCartByUser(User user) {
		Optional<Cart> cart = cartRepository.findByUser(user);
		
		
		return cart.get();
	}
	
}

