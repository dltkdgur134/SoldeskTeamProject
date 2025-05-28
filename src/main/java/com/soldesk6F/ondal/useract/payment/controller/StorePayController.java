package com.soldesk6F.ondal.useract.payment.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.entity.CartItemOption;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;
import com.soldesk6F.ondal.useract.cart.service.CartService;
import com.soldesk6F.ondal.useract.payment.dto.CartItemsDTO;
import com.soldesk6F.ondal.useract.payment.dto.UserInfoDTO;
import com.soldesk6F.ondal.useract.payment.entity.Payment;
import com.soldesk6F.ondal.useract.payment.service.PaymentFailLogService;
import com.soldesk6F.ondal.useract.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class StorePayController {

	private final CartService cartService;
	private final PaymentService paymentService;
	private final PaymentFailLogService paymentFailLogService;
	

	@PostMapping("/store/pay")
	public String tryPay(@RequestParam("cartUUID") UUID cartuuid , Model model, RedirectAttributes redirectAttributes,
			@RequestParam(value = "passCheckFail", required = false, defaultValue = "false") boolean passCheckFail) {
		Cart cart = cartService.findById(cartuuid);

		String validationResult = validateCartItems(cart, redirectAttributes);
		if (validationResult != null) return validationResult;
		
		List<CartItemsDTO> cids =  paymentService.getAllCartItems(cartuuid);
		int totalPrice = paymentService.getListTotalPrice(cids);
		int deliveryFee = cart.getStore().getDeliveryFee(); // 가게에서 배달료 가져오기
		int discountAmount = 1000; 
		int totalPayAmount = totalPrice + deliveryFee - discountAmount;
		if(totalPayAmount < 0) totalPayAmount = 0;
		UserInfoDTO uid = paymentService.getUserInfo(cartuuid);
		String storeName = paymentService.getCartStore(cartuuid);
		
		model.addAttribute("cart", cart);
		model.addAttribute("cids" , cids);
		model.addAttribute("totalPrice", totalPrice); // 메뉴+옵션 가격
	    model.addAttribute("deliveryFee", deliveryFee); // 배달료
	    model.addAttribute("discountAmount", discountAmount);
	    model.addAttribute("totalPayAmount", totalPayAmount); // 결제 총액
		model.addAttribute("cartId",cartuuid);
		model.addAttribute("storeName" ,storeName);
		model.addAttribute("successUrl","https://localhost:8443/store/paySuccess");
		model.addAttribute("failUrl" , "https://localhost:8443/store/payFail");
		model.addAttribute("userInfo" , uid);
		return "/content/pay";
	}
	
	@GetMapping("/store/paySuccess")
	public String showPaySuccessPage(@RequestParam("paymentKey") String paymentKey,@RequestParam("orderId") String orderId,
		    @RequestParam("amount") int amount,Model model) {
		
			if(paymentService.confirmPayment(paymentKey, orderId, amount)) {
				String nowOrderId = paymentService.findOrder(paymentKey);
				
				return "redirect:/user/order/" + nowOrderId;
			}else {
				return "/content/errorPage";
			}


	}

	@GetMapping("/store/payFail")
	public String showPaySuccessPage(
			@RequestParam("orderId") String orderId,
			@RequestParam("code") String code,
			@RequestParam("message") String message,
			@RequestParam(value = "paymentKey", required = false) String paymentKey,
            @AuthenticationPrincipal CustomUserDetails userDetails,
		   Model model) {
		
		String userUUIDString = userDetails.getUser().getUserUuidAsString();
		UUID userUUID = UUID.fromString(userUUIDString);
		
		paymentFailLogService.logOrderPaymentFailure(paymentKey, orderId, code, message, userUUID);
		model.addAttribute("code" , code);
		model.addAttribute("message" , message);
		
		
		return "/content/payFail";


	}
	
	
	private String validateCartItems(Cart cart, RedirectAttributes redirectAttributes) {
		for (CartItems item : cart.getCartItems()) {
			Menu currentMenu = item.getMenu();

			if (currentMenu == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "삭제된 메뉴가 장바구니에 있습니다.");
				return "redirect:/cart";
			}

			if (!item.getMenuName().equals(currentMenu.getMenuName())
					|| item.getMenuPrice() != currentMenu.getPrice()
					|| !item.getMenuImage().equals(currentMenu.getMenuImg())) {
				redirectAttributes.addFlashAttribute("errorMessage", item.getMenuName() + " 메뉴의 정보가 변경되었습니다.");
				return "redirect:/cart";
			}

			List<CartItemOption> selectedOptions = item.getCartItemOptions();
			List<String> validOptionNames = new ArrayList<>();
			List<Integer> validOptionPrices = new ArrayList<>();

			addValidOptions(currentMenu.getMenuOptions1(), currentMenu.getMenuOptions1Price(), validOptionNames, validOptionPrices);
			addValidOptions(currentMenu.getMenuOptions2(), currentMenu.getMenuOptions2Price(), validOptionNames, validOptionPrices);
			addValidOptions(currentMenu.getMenuOptions3(), currentMenu.getMenuOptions3Price(), validOptionNames, validOptionPrices);

			for (CartItemOption opt : selectedOptions) {
				int idx = validOptionNames.indexOf(opt.getOptionName());
				if (idx == -1 || idx >= validOptionPrices.size() || opt.getOptionPrice() != validOptionPrices.get(idx)) {
					redirectAttributes.addFlashAttribute("errorMessage", item.getMenuName() + " 메뉴의 옵션 정보가 변경되었습니다.");
					return "redirect:/cart";
				}
			}
		}
		return null;
	}
	
	private void addValidOptions(String optionRaw, String priceRaw, List<String> names, List<Integer> prices) {
		if (optionRaw == null || priceRaw == null) return;

		String[] nameParts = optionRaw.split(":");
		if (nameParts.length != 2) return;

		String[] optNames = nameParts[1].split("@@__@@");
		String[] optPrices = priceRaw.split("@@__@@");

		for (int i = 0; i < optNames.length; i++) {
			try {
				names.add(optNames[i].trim());
				prices.add(Integer.parseInt(optPrices[i].trim()));
			} catch (Exception e) {
				System.err.println("⚠️ 옵션 파싱 실패: " + optNames[i] + "/" + optPrices[i]);
			}
		}
	}
	
	

	
	
}
