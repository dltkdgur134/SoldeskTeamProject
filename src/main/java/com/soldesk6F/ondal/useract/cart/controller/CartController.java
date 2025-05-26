package com.soldesk6F.ondal.useract.cart.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.menu.service.MenuService;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.service.StoreService;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.service.UserService;
import com.soldesk6F.ondal.useract.cart.dto.CartAddRequestDto;
import com.soldesk6F.ondal.useract.cart.dto.CartItemOptionSaveDto;
import com.soldesk6F.ondal.useract.cart.dto.CartOptionDto;
import com.soldesk6F.ondal.useract.cart.dto.CartUpdateRequestDto;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.entity.CartItemOption;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;
import com.soldesk6F.ondal.useract.cart.repository.CartItemRepository;
import com.soldesk6F.ondal.useract.cart.service.CartItemService;
import com.soldesk6F.ondal.useract.cart.service.CartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;
	private final CartItemService cartItemService;
	private final CartItemRepository cartItemRepository;
	private final UserService userService;
	private final MenuService menuService;
	private final StoreService storeService;

	@GetMapping
	public String viewCart(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
	    UUID userUuid = userDetails.getUser().getUserUuid();
	    User user = userService.findUserByUuid(userUuid)
	            .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));

	    Cart cart = cartService.getCartByUser(user);
	    List<CartItems> cartItems = cart.getCartItems();

	    if (cartItems.isEmpty()) {
	        model.addAttribute("cart", cart);
	        model.addAttribute("cartItems", cartItems);
	        model.addAttribute("totalMenuPrice", 0);
	        model.addAttribute("deliveryFee", 0);
	        model.addAttribute("totalPrice", 0);
	        return "content/cart";
	    }

	    // ✅ 하나의 가게 정보 추출
	    Store store = cartItems.get(0).getMenu().getStore();
	    int deliveryFee = store.getDeliveryFee();

	    // ✅ 메뉴 총합 계산
	    int totalMenuPrice = cartItems.stream()
	    		.mapToInt(item -> item.getItemTotalPrice()) // 개당 가격 * 수량
	            .sum();

	    int totalPrice = totalMenuPrice + deliveryFee;

	    model.addAttribute("cart", cart);
	    model.addAttribute("cartItems", cartItems);
	    model.addAttribute("totalMenuPrice", totalMenuPrice);
	    model.addAttribute("deliveryFee", deliveryFee);
	    model.addAttribute("totalPrice", totalPrice); // 💡 프론트에 결제금액으로 전달

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
	
	@PostMapping("/api/cart/update-quantity")
	@ResponseBody
	public Map<String, Object> updateQuantity(@RequestBody CartUpdateRequestDto dto,
			 								@AuthenticationPrincipal CustomUserDetails userDetails) {
		User user = userService.findUserByUuid(userDetails.getUser().getUserUuid())
			.orElseThrow(() -> new IllegalStateException("User not found"));

		cartService.updateQuantity(dto.getCartItemUuid(), dto.getQuantity());
		int itemTotal = cartService.getUpdatedTotal(dto.getCartItemUuid());
		int cartTotal = cartService.getCartTotalPriceForUser(user);

		return Map.of(
			"totalPrice", itemTotal, "cartTotalPrice", cartTotal
		);
	}

	@PostMapping("/api/cart/delete")
	@ResponseBody
	public void deleteItem(@RequestBody Map<String, String> body) {
		UUID uuid = UUID.fromString(body.get("cartItemUuid"));
		cartService.deleteItem(uuid);
	}

	@GetMapping("/api/cart/total-price")
	@ResponseBody
	public Map<String, Object> getTotalCartPrice(@AuthenticationPrincipal CustomUserDetails userDetails) {
		User user = userService.findUserByUuid(userDetails.getUser().getUserUuid())
			.orElseThrow(() -> new IllegalStateException("유저 없음"));

		int totalPrice = cartService.getCartTotalPriceForUser(user);
		return Map.of("cartTotalPrice", totalPrice);
	}
	
	@GetMapping("/api/cart-item/options")
	@ResponseBody
	public List<CartOptionDto> getMenuOptions(@RequestParam("uuid") UUID cartItemUuid) {
		CartItems cartItem = cartItemRepository.findById(cartItemUuid)
			.orElseThrow(() -> new IllegalArgumentException("장바구니 항목 없음"));

		Menu menu = cartItem.getMenu();
		List<CartOptionDto> optionDtos = new ArrayList<>();

		parseOptions(menu.getMenuOptions1(), menu.getMenuOptions1Price(), optionDtos);
		parseOptions(menu.getMenuOptions2(), menu.getMenuOptions2Price(), optionDtos);
		parseOptions(menu.getMenuOptions3(), menu.getMenuOptions3Price(), optionDtos);

		List<CartItemOption> selectedOptions = cartItem.getCartItemOptions();

		for (CartOptionDto dto : optionDtos) {
			boolean isSelected = selectedOptions.stream()
				.anyMatch(opt -> opt.getOptionName().equals(dto.getName())
				              && opt.getGroupName().equals(dto.getGroupName()));
			dto.setSelected(isSelected);
		}
		return optionDtos;
	}
	
	private void parseOptions(String rawOption, String rawPrice, List<CartOptionDto> resultList) {
		if (!StringUtils.hasText(rawOption) || !StringUtils.hasText(rawPrice)) return;

		String[] nameParts = rawOption.split(":");
		if (nameParts.length != 2) return;

		String groupName = nameParts[0];
		String[] optionNames = nameParts[1].split("@@__@@");
		String[] optionPrices = rawPrice.split("@@__@@");

		if (optionNames.length != optionPrices.length) return;

		for (int i = 0; i < optionNames.length; i++) {
			try {
				int price = Integer.parseInt(optionPrices[i].trim());
				resultList.add(new CartOptionDto(groupName, optionNames[i].trim(), price));
			} catch (NumberFormatException e) {
				System.err.println("⚠️ 옵션 가격 숫자 변환 실패: " + optionPrices[i]);
			}
		}
	}
	
	@PostMapping("/api/cart-item/save-options")
	@ResponseBody
	public ResponseEntity<Map<String, String>> saveCartItemOptions(@RequestBody CartItemOptionSaveDto dto) {
		CartItems cartItem = cartItemRepository.findById(dto.getCartItemUuid())
			.orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));

		cartItemService.updateOptions(cartItem, dto.getOptions());

		Map<String, String> response = new HashMap<>();
		response.put("message", "옵션이 저장되었습니다.");

		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(response);
	}
	
	
}