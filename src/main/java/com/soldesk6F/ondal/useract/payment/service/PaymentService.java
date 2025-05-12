package com.soldesk6F.ondal.useract.payment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.payment.entity.Payment;
import com.soldesk6F.ondal.useract.payment.entity.Payment.PaymentMethod;
import com.soldesk6F.ondal.useract.payment.entity.Payment.PaymentStatus;
import com.soldesk6F.ondal.useract.payment.entity.Payment.PaymentUsageType;
import com.soldesk6F.ondal.useract.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void processOndalWalletPayment(UUID userUuid, String paymentKey, String tossOrderId, int amount) {
        // 유저 조회
        User user = userRepository.findById(userUuid).orElseThrow();

        // Payment 객체 생성 및 저장
        Payment payment = Payment.builder()
            .user(user)
            .paymentKey(paymentKey)
            .tossOrderId(tossOrderId)
            .amount(amount)
            .paymentMethod(PaymentMethod.CREDIT) // 추후 CASH도 지원 가능
            .paymentStatus(PaymentStatus.COMPLETED)
            .paymentUsageType(PaymentUsageType.ONDAL_WALLET)
            .build();

        paymentRepository.save(payment);

        // ondalWallet 금액 증가
        int currentWallet = user.getOndalWallet();
        user.setOndalWallet(currentWallet + amount);
        userRepository.save(user); // dirty checking 돼도 되지만 명시적으로 저장
    }
}
