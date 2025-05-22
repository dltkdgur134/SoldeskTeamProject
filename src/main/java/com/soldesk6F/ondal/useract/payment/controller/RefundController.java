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
	public String refundPayment(@RequestParam("refundReason") String cancelReason,
	                            @RequestParam("paymentMethod") String paymentMethod,
	                            @RequestParam(value = "paymentKey", required = false) String paymentKey,
	                            @RequestParam(value = "tossOrderId", required = false) String tossOrderId,
	                            @AuthenticationPrincipal CustomUserDetails userDetails,
	                            RedirectAttributes redirectAttributes) {
	    try {
	        UUID userUUID = UUID.fromString(userDetails.getUser().getUserUuidAsString());

	        if ("CASH".equalsIgnoreCase(paymentMethod) ||"CREDIT".equalsIgnoreCase(paymentMethod) ) {
	            if (paymentKey == null) {
	                throw new IllegalArgumentException("토스 결재 환불 실패");
	            }
	            paymentService.refundTossPayment(paymentKey, cancelReason, userUUID);

	        } else if ("ONDALPAY".equalsIgnoreCase(paymentMethod)) {
	            if (tossOrderId == null) {
	                throw new IllegalArgumentException("토스 결재 환불 실패");
	            }
	            paymentService.tryRefundOndalPay(tossOrderId, cancelReason, userUUID);

	        }

	        redirectAttributes.addFlashAttribute("success", "환불 성공");
	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("error", "환불 실패: " + e.getMessage());
	    }

	    return "redirect:/userPayHistory";
	}

}
