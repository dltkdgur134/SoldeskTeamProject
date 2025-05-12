package com.soldesk6F.ondal.useract.order.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.entity.CartItemOption;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;
import com.soldesk6F.ondal.useract.cart.service.CartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

	private final CartService cartService;

	@PostMapping("/pay")
	public String pay(@RequestParam("cartId") UUID cartId, Model model, RedirectAttributes redirectAttributes) {
		Cart cart = cartService.findById(cartId);
		
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

			// menu의 옵션을 파싱하여 비교 대상으로 구성
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
		model.addAttribute("cart", cart);
		return "content/pay";
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


