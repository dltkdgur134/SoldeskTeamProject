package com.soldesk6F.ondal.useract.payment.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.payment.service.PaymentService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class OndalWalletChargeController {

	private final PaymentService paymentService;
	private final UserRepository userRepository;

	@GetMapping("/walletCharge/success")
	public String ondalWalletSuccess(@RequestParam("paymentKey") String paymentKey, @RequestParam("orderId") String orderId,
			@RequestParam("amount") int amount,@AuthenticationPrincipal CustomUserDetails userDetails
			) {
		String userUUIDString = userDetails.getUser().getUserUuidAsString();
		UUID userUUID = UUID.fromString(userUUIDString);
		paymentService.confirmOndalWalletCharge(paymentKey, orderId, amount, userUUID);
		
		return "redirect:/userWallet"; // 충전 완료 페이지로 이동
	}

	@GetMapping("/walletCharge/fail")
	public String paymentFail(HttpServletResponse response) {
	    return "redirect:/userWallet"; // 실패 페이지
	}
	
	
	
}
