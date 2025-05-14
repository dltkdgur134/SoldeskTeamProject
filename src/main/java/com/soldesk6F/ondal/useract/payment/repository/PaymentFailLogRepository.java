package com.soldesk6F.ondal.useract.payment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.useract.payment.entity.PaymentFailLog;

public interface PaymentFailLogRepository extends JpaRepository<PaymentFailLog, UUID> {
	
}
