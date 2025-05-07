package com.soldesk6F.ondal.useract.order.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.service.CartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

	private final CartService cartService;

	@PostMapping("/pay")
	public String pay(@RequestParam UUID cartId, Model model) {
		Cart cart = cartService.findById(cartId);
		model.addAttribute("cart", cart);
		return "order/pay";
	}
}