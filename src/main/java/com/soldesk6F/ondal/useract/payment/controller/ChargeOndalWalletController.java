package com.soldesk6F.ondal.useract.payment.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.useract.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ondal-wallet")
@RequiredArgsConstructor
public class ChargeOndalWalletController {

    private final PaymentService paymentService;

    // 온달페이 결제 완료 처리
    @PostMapping("/charge")
    public ResponseEntity<String> chargeOndalWallet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String paymentKey,
            @RequestParam String orderId, // Toss에서 전달되는 orderId (우리가 저장하는 tossOrderId)
            @RequestParam int amount) {

        UUID userUuid = UUID.fromString(userDetails.getUser().getUserUuidAsString());

        // 결제 처리
        paymentService.processOndalWalletPayment(userUuid, paymentKey, orderId, amount);

        return ResponseEntity.ok("결제 및 충전 성공");
    }
}