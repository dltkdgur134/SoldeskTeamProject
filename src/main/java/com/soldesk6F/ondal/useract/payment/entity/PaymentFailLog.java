package com.soldesk6F.ondal.useract.payment.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "payment_fail_log")
public class PaymentFailLog {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "payment_fail_log_id", nullable = false, unique = true)
    private UUID paymentFailLogId;

    @ManyToOne
    @JoinColumn(name = "user_uuid", nullable = true)
    private User user;
    
    @Column(name = "payment_key", length = 200, nullable = true)
	private String paymentKey; // Toss paymentKey
    
    @Column(name = "toss_order_id", length = 64, nullable = false)
	private String tossOrderId;
    
    @Column(name = "fail_code", nullable = false)
    private String failCode;
    
    @Column(name = "fail_message", nullable = false)
    private String failMessage;

    @Column(name = "failed_at", nullable = false)
    private LocalDateTime failedAt;
    
    @Enumerated(EnumType.STRING)
	@Column(name = "payment_usage_type", nullable = false)
	private PaymentUsageType paymentUsageType;
    
    
    
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
    public PaymentFailLog(UUID paymentFailLogId, User user, String paymentKey, String tossOrderId,
                          String failCode, String failMessage, LocalDateTime failedAt,
                          PaymentUsageType paymentUsageType) {
        this.paymentFailLogId = paymentFailLogId;
        this.user = user;
        this.paymentKey = paymentKey;
        this.tossOrderId = tossOrderId;
        this.failCode = failCode;
        this.failMessage = failMessage;
        this.failedAt = failedAt;
        this.paymentUsageType = paymentUsageType;
    }
    
    public String getPaymentFailLogUuidAsString() {
		return paymentFailLogId != null ? paymentFailLogId.toString() : null;
	}
    
    
}

