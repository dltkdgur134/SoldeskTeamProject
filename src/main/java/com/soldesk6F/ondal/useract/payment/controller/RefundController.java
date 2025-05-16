package com.soldesk6F.ondal.useract.payment.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.useract.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class RefundController {
	private final PaymentService paymentService;

	@PostMapping("/refund")
	public String refundPayment(@RequestParam("paymentKey") String paymentKey,
	                            @RequestParam("refundReason") String cancelReason,
	                            @AuthenticationPrincipal CustomUserDetails userDetails,
	                            RedirectAttributes redirectAttributes) {
	    try {
	        // 로그인된 사용자 UUID 추출
	        String userUUIDString = userDetails.getUser().getUserUuidAsString();
	        UUID userUUID = UUID.fromString(userUUIDString);

	        // 환불 처리 (서비스에 userUuid 전달)
	        paymentService.refundTossPayment(paymentKey, cancelReason, userUUID);
	        redirectAttributes.addFlashAttribute("success", "환불 성공");
	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("error", "환불 실패: " + e.getMessage());
	    }

	    return "redirect:/userPayHistory"; // 환불 완료 후 이동할 페이지
	}

}
