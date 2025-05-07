package com.soldesk6F.ondal.useract.payment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soldesk6F.ondal.useract.payment.dto.CartItemsDTO;
import com.soldesk6F.ondal.useract.payment.dto.UserInfoDTO;
import com.soldesk6F.ondal.useract.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class StorePayController {

	private final PaymentService paymentService;
	

	@GetMapping("/store/pay")
	public String tryPay(@RequestParam("cartUUID")UUID cartuuid , Model model) {
		
		List<CartItemsDTO> cids =  paymentService.getAllCartItems(cartuuid);
		int totalPrice = paymentService.getListTotalPrice(cids);
		UserInfoDTO uid = paymentService.getUserInfo(cartuuid);
		model.addAttribute("cids" , cids);
		model.addAttribute("totalPrice" , totalPrice);
		model.addAttribute("userInfo" , uid);
		return "/content/pay";
	}
	
	
	
	
	
	
}
