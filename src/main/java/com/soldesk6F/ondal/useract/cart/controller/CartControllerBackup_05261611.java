//package com.soldesk6F.ondal.useract.cart.controller;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.soldesk6F.ondal.login.CustomUserDetails;
//import com.soldesk6F.ondal.menu.entity.Menu;
//import com.soldesk6F.ondal.menu.service.MenuService;
//import com.soldesk6F.ondal.store.entity.Store;
//import com.soldesk6F.ondal.store.service.StoreService;
//import com.soldesk6F.ondal.user.entity.User;
//import com.soldesk6F.ondal.user.service.UserService;
//import com.soldesk6F.ondal.useract.cart.dto.CartInitRequestDto;
//import com.soldesk6F.ondal.useract.cart.dto.CartItemOptionSaveDto;
//import com.soldesk6F.ondal.useract.cart.dto.CartOptionDto;
//import com.soldesk6F.ondal.useract.cart.entity.Cart;
//import com.soldesk6F.ondal.useract.cart.entity.CartItemOption;
//import com.soldesk6F.ondal.useract.cart.entity.CartItems;
//import com.soldesk6F.ondal.useract.cart.repository.CartItemsRepository;
//import com.soldesk6F.ondal.useract.cart.service.CartItemService;
//import com.soldesk6F.ondal.useract.cart.service.CartService;
//
//import lombok.RequiredArgsConstructor;
//
//@Controller
//@RequestMapping("/cart")
//@RequiredArgsConstructor
//public class CartControllerBackup_05261611 {
//
//	private final CartService cartService;
//	private final CartItemService cartItemService;
//	private final CartItemsRepository cartItemsRepository;
//	private final UserService userService;
//	private final MenuService menuService;
//	private final StoreService storeService;
//
//	@GetMapping
//	public String viewCart(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
//		UUID userUuid = userDetails.getUser().getUserUuid();
//		User user = userService.findUserByUuid(userUuid)
//				.orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));
//		/*
//		 * model.addAttribute("cart", cart); model.addAttribute("cartItems",
//		 * cart.getCartItems()); model.addAttribute("totalPrice", cart.getTotalPrice());
//		 */
//		return "content/cart";
//	}
//
//	/*
//	 * @GetMapping("/api/cart/total-price")
//	 * 
//	 * @ResponseBody public Map<String, Object>
//	 * getTotalCartPrice(@AuthenticationPrincipal CustomUserDetails userDetails) {
//	 * User user = userService.findUserByUuid(userDetails.getUser().getUserUuid())
//	 * .orElseThrow(() -> new IllegalStateException("유저 없음"));
//	 * 
//	 * int totalPrice = cartService.getCartTotalPriceForUser(user); return
//	 * Map.of("cartTotalPrice", totalPrice); }
//	 */
//	@GetMapping("/api/cart-item/options")
//	@ResponseBody
//	public List<CartOptionDto> getMenuOptions(@RequestParam("uuid") UUID cartItemUuid) {
//		CartItems cartItem = cartItemsRepository.findById(cartItemUuid)
//			.orElseThrow(() -> new IllegalArgumentException("장바구니 항목 없음"));
//
//		Menu menu = cartItem.getMenu();
//		List<CartOptionDto> optionDtos = new ArrayList<>();
//
//		parseOptions(menu.getMenuOptions1(), menu.getMenuOptions1Price(), optionDtos);
//		parseOptions(menu.getMenuOptions2(), menu.getMenuOptions2Price(), optionDtos);
//		parseOptions(menu.getMenuOptions3(), menu.getMenuOptions3Price(), optionDtos);
//
//		List<CartItemOption> selectedOptions = cartItem.getCartItemOptions();
//
//		for (CartOptionDto dto : optionDtos) {
//			boolean isSelected = selectedOptions.stream()
//				.anyMatch(opt -> opt.getOptionName().equals(dto.getName()) && opt.getGroupName().equals(dto.getGroupName()));
//			dto.setSelected(isSelected);
//		}
//		return optionDtos;
//	}
//	
//	private void parseOptions(String rawOption, String rawPrice, List<CartOptionDto> resultList) {
//		if (!StringUtils.hasText(rawOption) || !StringUtils.hasText(rawPrice)) return;
//
//		String[] nameParts = rawOption.split(":");
//		if (nameParts.length != 2) return;
//
//		String groupName = nameParts[0];
//		String[] optionNames = nameParts[1].split("@@__@@");
//		String[] optionPrices = rawPrice.split("@@__@@");
//
//		if (optionNames.length != optionPrices.length) return;
//
//		for (int i = 0; i < optionNames.length; i++) {
//			try {
//				int price = Integer.parseInt(optionPrices[i].trim());
//				resultList.add(new CartOptionDto(groupName, optionNames[i].trim(), price));
//			} catch (NumberFormatException e) {
//				System.err.println("⚠️ 옵션 가격 숫자 변환 실패: " + optionPrices[i]);
//			}
//		}
//	}
//	
//	@PostMapping("/api/cart-item/save-options")
//	@ResponseBody
//	public ResponseEntity<Map<String, String>> saveCartItemOptions(@RequestBody CartItemOptionSaveDto dto) {
//		CartItems cartItem = cartItemsRepository.findById(dto.getCartItemUuid())
//			.orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));
//
//		cartItemService.updateOptions(cartItem, dto.getOptions());
//
//		Map<String, String> response = new HashMap<>();
//		response.put("message", "옵션이 저장되었습니다.");
//
//		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
//	}
//	
//	@PostMapping("/api/init")
//	@ResponseBody
//	public ResponseEntity<Map<String, String>> initCart(@RequestBody CartInitRequestDto dto) {
//		// 1. 사용자 조회
//		User user = userService.findUserByUuid(dto.getUserUUID())
//			.orElseThrow(() -> new IllegalArgumentException("유저 없음"));
//
//		// 2. storeId는 items의 첫 항목에서 가져옴
//		if (dto.getItems().isEmpty()) throw new IllegalArgumentException("아이템 없음");
//
//		Store store = storeService.findById(dto.getItems().get(0).getStoreId());
//
//		// 3. 장바구니 생성
//		Cart cart = cartService.createCart(user, store, dto.getItems());
//
//		return ResponseEntity.ok(Map.of("cartId", cart.getCartId().toString()));
//	}
//	
//}