package com.soldesk6F.ondal.useract.payment.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.useract.order.entity.Order.OrderToOwner;
import com.soldesk6F.ondal.useract.payment.dto.PaymentHistoryDTO;
import com.soldesk6F.ondal.useract.payment.entity.Payment;
import com.soldesk6F.ondal.useract.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {
	
	private final PaymentRepository paymentRepository;
	
	
	public List<PaymentHistoryDTO> getPaymentHistoryByUserUUID(String userUUID) {
	    UUID uuid = UUID.fromString(userUUID);
	    List<Payment> payments = paymentRepository.findByUserUserUuidOrderByApprovedAtDesc(uuid);

	    return payments.stream()
	            .map(payment -> PaymentHistoryDTO.builder()
	                    .paymentMethod(payment.getPaymentMethod())
	                    .amount(payment.getAmount())
	                    .requestedAt(payment.getRequestedAt())
	                    .approvedAt(payment.getApprovedAt())
	                    .paymentStatus(payment.getPaymentStatus())
	                    .refundReason(payment.getRefundReason())
	                    .paymentUsageType(payment.getPaymentUsageType())
	                    .tossOrderId(payment.getTossOrderId())
	                    .build())
	            .toList();
	}
	
	public List<PaymentHistoryDTO> getFilteredPaymentHistory(String userUUID, String status, String usage, Integer days) {
	    Payment.PaymentStatus statusEnum = null;
	    Payment.PaymentUsageType usageEnum = null;
	    LocalDateTime since = null;

	    if (status != null && !"ALL".equalsIgnoreCase(status)) {
	        statusEnum = Payment.PaymentStatus.valueOf(status);
	    }
	    if (usage != null && !usage.isEmpty()) {
	        usageEnum = Payment.PaymentUsageType.valueOf(usage);
	    }
	    if (days != null) {
	        since = LocalDateTime.now().minusDays(days);
	    }

	    return paymentRepository.findFilteredHistory(UUID.fromString(userUUID), statusEnum, usageEnum, since)
	            .stream()
	            .map(payment -> {
	                OrderToOwner orderToOwner = payment.getOrder() != null ? payment.getOrder().getOrderToOwner() : null;
	                return PaymentHistoryDTO.fromEntity(payment, orderToOwner);
	            })
	            .collect(Collectors.toList());
	}


	
	
}
