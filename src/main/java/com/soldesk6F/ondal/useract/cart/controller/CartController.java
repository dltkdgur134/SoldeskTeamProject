package com.soldesk6F.ondal.useract.cart.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.menu.service.MenuService;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.service.StoreService;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.service.UserService;
import com.soldesk6F.ondal.useract.cart.dto.CartInitRequestDto;
import com.soldesk6F.ondal.useract.cart.dto.CartItemOptionSaveDto;
import com.soldesk6F.ondal.useract.cart.dto.CartOptionDto;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.entity.CartItemOption;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;
import com.soldesk6F.ondal.useract.cart.entity.CartStatus;
import com.soldesk6F.ondal.useract.cart.repository.CartItemsRepository;
import com.soldesk6F.ondal.useract.cart.repository.CartRepository;
import com.soldesk6F.ondal.useract.cart.service.CartItemService;
import com.soldesk6F.ondal.useract.cart.service.CartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;
	private final CartItemService cartItemService;
	private final CartItemsRepository cartItemsRepository;
	private final CartRepository cartRepository;
	private final UserService userService;
	private final MenuService menuService;
	private final StoreService storeService;

	@GetMapping
	public String viewCart(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
		UUID userUuid = userDetails.getUser().getUserUuid();
		User user = userService.findUserByUuid(userUuid)
				.orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));
		// 기존 Cart 확인 후 localStorage에 저장할 데이터 반환 후 삭제
		Optional<Cart> optionalCart = cartRepository.findByUser(user)
				.filter(cart -> cart.getStatus() == CartStatus.PENDING || cart.getStatus() == CartStatus.CANCELED);
		if (optionalCart.isPresent()) {
			Cart cart = optionalCart.get();

			List<Map<String, Object>> restoredItems = new ArrayList<>();
			for (CartItems item : cart.getCartItems()) {
				Map<String, Object> data = new HashMap<>();
				data.put("menuId", item.getMenu().getMenuId());
				data.put("storeId", cart.getStore().getStoreId());
				data.put("menuName", item.getMenu().getMenuName());
				data.put("menuImage", item.getMenu().getMenuImg());
				data.put("price", item.getMenu().getPrice());
				data.put("quantity", item.getQuantity());

				List<CartOptionDto> options = item.getCartItemOptions().stream().map(opt ->
					new CartOptionDto(opt.getGroupName(), opt.getOptionName(), opt.getOptionPrice(), true)
				).collect(Collectors.toList());
				data.put("options", options);

				restoredItems.add(data);
			}

			model.addAttribute("restoredCartItems", restoredItems);

			cartRepository.delete(cart);
		}

		return "content/cart";
/*	    Cart cart = cartService.getCartByUser(user);
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

	    return "content/cart";*/
	}

	@GetMapping("/api/cart-item/options")
	@ResponseBody
	public List<CartOptionDto> getMenuOptions(@RequestParam("uuid") UUID cartItemUuid) {
		CartItems cartItem = cartItemsRepository.findById(cartItemUuid)
			.orElseThrow(() -> new IllegalArgumentException("장바구니 항목 없음"));

		Menu menu = cartItem.getMenu();
		List<CartOptionDto> optionDtos = new ArrayList<>();

		parseOptions(menu.getMenuOptions1(), menu.getMenuOptions1Price(), optionDtos);
		parseOptions(menu.getMenuOptions2(), menu.getMenuOptions2Price(), optionDtos);
		parseOptions(menu.getMenuOptions3(), menu.getMenuOptions3Price(), optionDtos);

		List<CartItemOption> selectedOptions = cartItem.getCartItemOptions();

		for (CartOptionDto dto : optionDtos) {
			boolean isSelected = selectedOptions.stream()
				.anyMatch(opt -> opt.getOptionName().equals(dto.getName()) && opt.getGroupName().equals(dto.getGroupName()));
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
		CartItems cartItem = cartItemsRepository.findById(dto.getCartItemUuid())
			.orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));

		cartItemService.updateOptions(cartItem, dto.getOptions());

		Map<String, String> response = new HashMap<>();
		response.put("message", "옵션이 저장되었습니다.");

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
	}

	@PostMapping("/api/init")
	@ResponseBody
	public ResponseEntity<Map<String, String>> initCart(@RequestBody CartInitRequestDto dto) {
		User user = userService.findUserByUuid(dto.getUserUUID())
				.orElseThrow(() -> new IllegalArgumentException("유저 없음"));

		if (dto.getItems().isEmpty()) throw new IllegalArgumentException("아이템 없음");
		Store store = storeService.findById(dto.getItems().get(0).getStoreId());
		Cart cart = cartService.createCart(user, store, dto.getItems());
		return ResponseEntity.ok(Map.of("cartId", cart.getCartId().toString()));
	}
	
	@GetMapping("/api/restore")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> restoreCart(@RequestParam UUID userUuid) {
		try {
			User user = userService.findUserByUuid(userUuid)
					.orElseThrow(() -> new IllegalArgumentException("유저 없음"));
			
			Optional<Cart> optionalCart = cartService.findLatestCartByUser(user);
			if (optionalCart.isEmpty()) {
				return ResponseEntity.ok(Map.of("restored", false));
			}
			
			Cart cart = optionalCart.get();
			
			if (cart.getStatus() != CartStatus.PENDING && cart.getStatus() != CartStatus.CANCELED) {
				return ResponseEntity.ok(Map.of("restored", false));
			}
			
			List<Map<String, Object>> restoredItems = cart.getCartItems().stream().map(item -> {
				Map<String, Object> menuData = new HashMap<>();
				menuData.put("menuId", item.getMenu().getMenuId());
				menuData.put("storeId", cart.getStore().getStoreId());
				menuData.put("menuName", item.getMenu().getMenuName());
				menuData.put("menuImage", item.getMenu().getMenuImg());
				menuData.put("price", item.getMenu().getPrice());
				menuData.put("quantity", item.getQuantity());
				
				List<Map<String, Object>> options = item.getCartItemOptions().stream().map(opt -> {
					Map<String, Object> optMap = new HashMap<>();
					optMap.put("groupName", opt.getGroupName());
					optMap.put("name", opt.getOptionName());
					optMap.put("price", opt.getOptionPrice());
					optMap.put("selected", true);
					return optMap;
				}).collect(Collectors.toList());
				
				menuData.put("options", options);
				return menuData;
			}).collect(Collectors.toList());
			
			// delete from DB after collecting data
			cartService.deleteCart(cart);
			
			return ResponseEntity.ok(Map.of(
					"restored", true,
					"cartItems", restoredItems
					));
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity
				.status(500)
				.body(Map.of("error", "restoreCart 서버 오류: " + e.getMessage()));
		}
	}
}