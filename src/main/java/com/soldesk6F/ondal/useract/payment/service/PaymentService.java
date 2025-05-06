package com.soldesk6F.ondal.useract.payment.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.login.CustomUserDetails;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;
import com.soldesk6F.ondal.useract.cart.repository.CartItemsRepository;
import com.soldesk6F.ondal.useract.cart.repository.CartRepository;
import com.soldesk6F.ondal.useract.payment.dto.CartItemsDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final CartRepository cartRepository;
	private final CartItemsRepository cartItemsRepository;

	public List<CartItemsDTO> getAllCartItems(UUID cartUUID) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("로그인된 사용자가 없습니다");
		}
		User user = null;
		if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
			user = customUserDetails.getUser();
		}
		UUID nowSessionUUID;

		if (user != null) {
			nowSessionUUID = user.getUserUuid();
			Optional<Cart> optCart = cartRepository.findById(cartUUID);
			Cart cart = optCart.orElseThrow(() -> new IllegalArgumentException("해당 카트가 존재하지 않습니다."));
			if (cart.getUser().getUserUuid().equals(nowSessionUUID)) {

				List<CartItems> items = cartItemsRepository.findByCart_CartId(cart.getCartId());
				if (items.isEmpty()) {
					throw new IllegalArgumentException("카트에 담긴 아이템이 없습니다");
				} else {
					return items.stream().map(item -> {
						return CartItemsDTO.builder().menuName(item.getMenu().getMenuName())
								.menuPrice(item.getMenu().getPrice())
								.optionNames(Arrays.asList(item.getOptions().split("온달")))
								.optionTotalPrice(item.getOptionTotalPrice()).quantity(item.getQuantity())
								.totalPrice(item.getItemTotalPrice()).menuImg(item.getMenu().getMenuImg()).build();

					}).collect(Collectors.toList());
				}

			}

		}
	    throw new IllegalStateException("결제 요청을 처리할 수 없습니다");
	}
	
	
	public int getListTotalPrice(List<CartItemsDTO> cids) {
		int total = 0;
		for(CartItemsDTO cid : cids) {
			total += cid.getTotalPrice();
		}
		return total;
	}
	
	
	
	
	
}
