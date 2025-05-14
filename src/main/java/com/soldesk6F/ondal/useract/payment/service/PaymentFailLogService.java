package com.soldesk6F.ondal.useract.payment.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.payment.entity.PaymentFailLog;
import com.soldesk6F.ondal.useract.payment.repository.PaymentFailLogRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Service
public class PaymentFailLogService {

    private final PaymentFailLogRepository paymentFailLogRepository;
    private final UserRepository userRepository;

    public PaymentFailLogService(PaymentFailLogRepository paymentFailLogRepository, UserRepository userRepository) {
        this.paymentFailLogRepository = paymentFailLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logWalletPaymentFailure(String paymentKey, String tossOrderId, String failCode, String failMessage, UUID userUUID) {
        User user = null;
        if (userUUID != null) {
            user = userRepository.findById(userUUID).orElse(null);
        }

        PaymentFailLog failLog = PaymentFailLog.builder()
                .paymentKey(paymentKey)
                .tossOrderId(tossOrderId)
                .failCode(failCode)
                .failMessage(failMessage)
                .failedAt(LocalDateTime.now())
                .user(user)
                .paymentUsageType(PaymentFailLog.PaymentUsageType.ONDAL_WALLET)
                .build();

        if (failLog != null) {
            paymentFailLogRepository.save(failLog);
            paymentFailLogRepository.flush();
        } else {
            System.err.println("failLog is null, can't save to DB.");
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
	public void logOrderPaymentFailure(String paymentKey, String tossOrderId, String failCode, String failMessage, UUID userUUID) {
	    User user = null;
	    if (userUUID != null) {
	        user = userRepository.findById(userUUID).orElse(null); // nullable
	    }

	    PaymentFailLog failLog = PaymentFailLog.builder()
	            .paymentKey(paymentKey)
	            .tossOrderId(tossOrderId)
	            .failCode(failCode)
	            .failMessage(failMessage)
	            .failedAt(LocalDateTime.now())
	            .user(user)
	            .paymentUsageType(PaymentFailLog.PaymentUsageType.ORDER_PAYMENT) // or ORDER_PAYMENT
	            .build();

	    if (failLog != null) {
            paymentFailLogRepository.save(failLog);
            paymentFailLogRepository.flush();
        } else {
            System.err.println("failLog is null, can't save to DB.");
        }
	}
    
    
    
    
}

