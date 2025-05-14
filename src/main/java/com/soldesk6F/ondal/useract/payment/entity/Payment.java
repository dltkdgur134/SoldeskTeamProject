package com.soldesk6F.ondal.useract.payment.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.order.entity.Order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "payment")
public class Payment {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "payment_id", nullable = false, unique = true)
	private UUID paymentId;

	@ManyToOne
	@JoinColumn(name = "user_uuid", nullable = true)
	private User user;

	@OneToOne
	@JoinColumn(name = "order_id", unique = true, nullable = true)
	private Order order;

	@Column(name = "payment_key", length = 200, nullable = false, unique = true)
	private String paymentKey; // Toss paymentKey

	@Column(name = "toss_order_id", length = 64, nullable = false)
	private String tossOrderId;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false)
	private PaymentMethod paymentMethod;

	@Column(name = "amount", nullable = false)
	private int amount;

	@Column(name = "requested_at", nullable = false)
	private LocalDateTime requestedAt;

	@Column(name = "approved_at")
	private LocalDateTime approvedAt;

	@CreationTimestamp
	@Column(name = "payment_time", nullable = false, updatable = false)
	private LocalDateTime paymentTime;

	@UpdateTimestamp
	@Column(name = "updated_date", nullable = false)
	private LocalDateTime updatedDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false)
	private PaymentStatus paymentStatus;

	@Column(name = "refund_reason", length = 50, nullable = true)
	private String refundReason;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_usage_type", nullable = false)
	private PaymentUsageType paymentUsageType;

	public enum PaymentMethod {
		CASH("현금"), CREDIT("카드");

		private final String description;

		PaymentMethod(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	public enum PaymentStatus {
		COMPLETED("결제 완료"),
		CANCELED("결제 취소"),
		WAITING_FOR_REFUND("환불 대기"),
		REFUNDED("환불 완료");

		private final String description;

		PaymentStatus(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	public enum PaymentUsageType {
		ORDER_PAYMENT("주문 결재"), // 배달 주문 결제용
		ONDAL_WALLET("온달 지갑 충전"); // 온달 지갑 충전용

		private final String description;

		PaymentUsageType(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	@Builder
	public Payment(User user,Order order,String paymentKey,String tossOrderId, PaymentMethod paymentMethod, int amount, PaymentStatus paymentStatus,
			PaymentUsageType paymentUsageType,String refundReason) {
		super();
		this.user = user;
		this.order = order;
		this.paymentKey = paymentKey;
		this.tossOrderId = tossOrderId;
		this.paymentMethod = paymentMethod;
		this.amount = amount;
		this.paymentUsageType = paymentUsageType;
		this.paymentStatus = paymentStatus;
		this.refundReason = refundReason;
	}

	public String getPaymentUuidAsString() {
		return paymentId != null ? paymentId.toString() : null;
	}

}

