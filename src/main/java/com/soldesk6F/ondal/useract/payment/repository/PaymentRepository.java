package com.soldesk6F.ondal.useract.payment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soldesk6F.ondal.useract.payment.entity.Payment;



public interface PaymentRepository extends JpaRepository<Payment, UUID>{

	@Modifying
	@Query("UPDATE Payment p SET p.paymentStatus = :paymentStatus WHERE p.paymentKey = :paymentKey")
	int updatePaymentStatusWithPaymentKey(@Param("paymentKey") String paymentKey,
	                                      @Param("paymentStatus") Payment.PaymentStatus status);
	
	
	
	
}
	