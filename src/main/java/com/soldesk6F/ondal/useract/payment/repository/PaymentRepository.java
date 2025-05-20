package com.soldesk6F.ondal.useract.payment.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.useract.payment.entity.Payment;



public interface PaymentRepository extends JpaRepository<Payment, UUID>{

	
	
	
	@Modifying
	@Query("UPDATE Payment p SET p.paymentStatus = :paymentStatus WHERE p.paymentKey = :paymentKey")
	int updatePaymentStatusWithPaymentKey(@Param("paymentKey") String paymentKey,
	                                      @Param("paymentStatus") Payment.PaymentStatus status);

	List<Payment> findByUser_UserUuid(UUID uuid);
	List<Payment> findByUserUserUuidOrderByApprovedAtDesc(UUID userUuid);
	Optional<Payment> findByTossOrderId(String tossOrderId);
	
	@Query("SELECT p FROM Payment p LEFT JOIN FETCH p.order o WHERE p.user.userUuid = :userUuid "
		     + "AND (:status IS NULL OR p.paymentStatus = :status) "
		     + "AND (:usage IS NULL OR p.paymentUsageType = :usage) "
		     + "AND (:since IS NULL OR p.requestedAt >= :since)")
		List<Payment> findFilteredHistory(@Param("userUuid") UUID userUuid,
		                                  @Param("status") Payment.PaymentStatus status,
		                                  @Param("usage") Payment.PaymentUsageType usage,
		                                  @Param("since") LocalDateTime since);

	Optional<Payment> findByPaymentKey(String paymentKey);

	@Modifying
	@Query("UPDATE Payment p SET p.paymentStatus = :status WHERE p.tossOrderId = :tossOrderId")
	int updatePaymentStatusWithTossOrderId(@Param("tossOrderId") String tossOrderId,
	                                       @Param("status") Payment.PaymentStatus status);

	@Query("SELECT p FROM Payment p WHERE p.order IS NOT NULL AND p.order.id = :orderId")
	Optional<Payment> findByOrder_OrderId(@Param("orderId") UUID orderId);
	
}