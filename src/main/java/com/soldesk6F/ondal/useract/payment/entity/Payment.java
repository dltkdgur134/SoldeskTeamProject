package com.soldesk6F.ondal.useract.payment.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.useract.order.entity.Order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
	@Column(name = "payment_id", nullable = false,unique = true)
	private UUID paymentId;
	
	@OneToOne
	@JoinColumn(name = "order_id" , nullable = false, unique = true)
	private Order order;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method",nullable = false)
	private PaymentMethod paymentMethod;
	
	@Column(name = "amount", nullable = false)
	private int amount;
	
	@CreationTimestamp
	@Column(name = "payment_time" ,nullable = false,updatable = false)
	private LocalDateTime paymentTime;
	
	@UpdateTimestamp
	@Column(name = "updated_date",nullable =  false)
	private LocalDateTime updatedDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status" , nullable = false)
	private PaymentStatus paymentStatus;
	
	@Column(name = "refund_reason",length = 50,nullable = true)
	private String refundReason;
	
	public enum PaymentMethod{
		CASH("현금"),
		CREDIT("카드");
		
		private final String description;
		
		PaymentMethod(String description){
			this.description = description;
		}
		public String getDescription() {
            return description;
        }
	}
	
	public enum PaymentStatus{
		COMPLETED("결제 완료"),
		CANCLED("결제 취소"),
		WAITING_FOR_REFUND("환불 대기"),
		REFUNDED("환불 완료");
		
		private final String description;
		
		PaymentStatus(String description){
			this.description = description;
		}
		public String getDescription() {
			return description;
		}
	}

	@Builder
	public Payment(Order order, PaymentMethod paymentMethod, int amount, PaymentStatus paymentStatus,
			String refundReason) {
		super();
		this.order = order;
		this.paymentMethod = paymentMethod;
		this.amount = amount;
		this.paymentStatus = paymentStatus;
		this.refundReason = refundReason;
	}
	

	
	
}
