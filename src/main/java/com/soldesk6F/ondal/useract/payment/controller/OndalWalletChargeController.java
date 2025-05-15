package com.soldesk6F.ondal.useract.payment.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.user.service.UserService;
import com.soldesk6F.ondal.useract.payment.entity.Payment;
import com.soldesk6F.ondal.useract.payment.repository.PaymentRepository;
import com.soldesk6F.ondal.useract.payment.service.PaymentFailLogService;
import com.soldesk6F.ondal.useract.payment.service.PaymentService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class OndalWalletChargeController {

	private final PaymentService paymentService;
	private final PaymentFailLogService paymentFailLogService;
	private final UserService userService;
	private final UserRepository userRepository;

	@GetMapping("/walletCharge/success")
	public String ondalWalletSuccess(@RequestParam("paymentKey") String paymentKey,
			@RequestParam("orderId") String orderId, @RequestParam("amount") int amount,
			@AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes redirectAttributes) {
		String userUUIDString = userDetails.getUser().getUserUuidAsString();
		UUID userUUID = UUID.fromString(userUUIDString);
		try {
			paymentService.confirmOndalWalletCharge(paymentKey, orderId, amount, userUUID);

			redirectAttributes.addFlashAttribute("success", amount + "원이 온달 지갑으로 충전되었습니다.");
		} catch (IllegalArgumentException e) {
			// 서비스에서 토스 승인 실패 후 던진 예외
			redirectAttributes.addFlashAttribute("error", "충전에 실패했습니다: " + e.getMessage());
		} catch (Exception e) {
			// 기타 예상치 못한 예외 처리
			redirectAttributes.addFlashAttribute("error", "알 수 없는 오류가 발생했습니다. 관리자에게 문의해주세요.");
		}

		return "redirect:/userWallet"; // 충전 완료 페이지로 이동
	}

	@GetMapping("/walletCharge/fail")
	public String ondalWalletFail(@RequestParam("orderId") String orderId, @RequestParam("code") String code,
			@RequestParam("message") String message,
			@RequestParam(value = "paymentKey", required = false) String paymentKey,
			@AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes redirectAttributes) {
		UUID userUUID = null;
		if (userDetails != null && userDetails.getUser() != null) {
			userUUID = userDetails.getUser().getUserUuid();
		}

		// 로그 저장
		paymentFailLogService.logWalletPaymentFailure(paymentKey != null ? paymentKey : "N/A", orderId, code, message,
				userUUID);

		// 사용자 알림
		redirectAttributes.addFlashAttribute("error", "지갑 충전이 실패했습니다. (" + message + ")");

		return "redirect:/userWallet";
	}

	@PostMapping("/checkUserPasswordAndGoPayHistory")
	public String checkUserPasswordAndGoPayHistory(
	        @RequestParam(value = "currentPassword", required = false) String currentPassword,
	        @AuthenticationPrincipal CustomUserDetails userDetails,
	        RedirectAttributes redirectAttributes) {

	    boolean isCorrect = userService.checkPassword(userDetails, currentPassword, redirectAttributes);

	    if (isCorrect) {
	    	redirectAttributes.addFlashAttribute("success", "비밀번호 확인 성공");
	        return "redirect:/userPayHistory";
	    }else {
	    	redirectAttributes.addFlashAttribute("error", "비밀번호가 틀렸습니다.");
		}

	    return "redirect:/userWallet";
	}

}
