package com.soldesk6F.ondal.useract.cart.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.menu.service.MenuService;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.service.StoreService;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.service.UserService;
import com.soldesk6F.ondal.useract.cart.dto.CartAddRequestDto;
import com.soldesk6F.ondal.useract.cart.dto.CartUpdateRequestDto;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.service.CartItemService;
import com.soldesk6F.ondal.useract.cart.service.CartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;
	private final CartItemService cartItemService;
	private final UserService userService;
	private final MenuService menuService;
	private final StoreService storeService;

	@GetMapping
	public String viewCart(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
		UUID userUuid = userDetails.getUser().getUserUuid();
		User user = userService.findUserByUuid(userUuid)
				.orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));

		Cart cart = cartService.getCartByUser(user);

		model.addAttribute("cart", cart);
		model.addAttribute("cartItems", cart.getCartItems());
		model.addAttribute("totalPrice", cart.getTotalPrice());

		return "content/cart";
	}
	
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<?> addToCart(@RequestBody CartAddRequestDto dto,
	                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
		User user = userService.findUserByUuid(userDetails.getUser().getUserUuid())
			.orElseThrow(() -> new IllegalStateException("유저 없음"));

		Menu menu = menuService.findById(dto.getMenuId()); // 예외처리 필요
		Store store = storeService.findById(dto.getStoreId()); // 예외처리 필요

		Cart cart = cartService.getCartByUserAndStore(user, store); // 자동 생성 포함

		cartItemService.addItemToCart(cart, menu, store, dto.getQuantity(), dto.getOptions());
		

		return ResponseEntity.ok(Map.of("message", "장바구니에 담았습니다!"));
	}
	
//	@PostMapping("/api/cart/update-quantity")
//	@ResponseBody
//	public Map<String, Object> updateQuantity(@RequestBody CartUpdateRequestDto dto) {
//		cartService.updateQuantity(dto.getCartItemUuid(), dto.getQuantity());
//		int itemTotal = cartService.getUpdatedTotal(dto.getCartItemUuid());
//		int cartTotal = cartService.getCartTotalPriceForUser();
//
//		return Map.of(
//			"totalPrice", itemTotal, "cartTotalPrice", cartTotal
//		);
//	}
//
//	@PostMapping("/api/cart/delete")
//	@ResponseBody
//	public void deleteItem(@RequestBody Map<String, String> body) {
//		cartService.deleteItem(UUID.fromString(body.get("cartItemUuid")));
//	}
//	
//	@GetMapping("/api/cart/total-price")
//	@ResponseBody
//	public Map<String, Object> getTotalCartPrice() {
//		int totalPrice = cartService.getCartTotalPriceForUser();
//		return Map.of("cartTotalPrice", totalPrice);
//	}
	
	
}