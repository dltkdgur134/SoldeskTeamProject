package com.soldesk6F.ondal.owner.sales.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.useract.order.entity.Order;

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
@Table(name = "sales")
public class Sales {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "sales_id", nullable = false, unique = true)
	private UUID salesId; // 매출 PK

	@Column(name = "sales_date", nullable = false)
	private LocalDateTime salesDate; // 매출 발생일자

	@ManyToOne
	@JoinColumn(name = "s_management_id", nullable = false)
	private SalesManagement salesManagement; // 매출 관리와 연결 (N:1)

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private Owner owner; // 가게 소유자 (N:1)

	@ManyToOne
	@JoinColumn(name = "store_id", nullable = false)
	private Store store; // 매출이 발생한 가게 (N:1)

	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	private Order order; // 주문과 연결 (N:1)

	@Column(name = "menu_name", nullable = false, length = 100)
	private String menuName; // 메뉴 이름

	@Column(name = "price", nullable = false)
	private int price; // 가격

	@Column(name = "vat", nullable = false)
	private int vat; // 부가세

	@Column(name = "total_sales", nullable = false)
	private int totalSales; // 총 매출액

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false, length = 20)
	private PaymentStatus paymentStatus; // 결제 상태

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false, length = 20)
	private PaymentMethod paymentMethod; // 결제 방법

	@Enumerated(EnumType.STRING)
	@Column(name = "sales_status", nullable = false, length = 20)
	private SalesStatus salesStatus; // 매출 상태

	@Column(name = "sales_title", length = 100)
	private String salesTitle; // 매출 관련 제목

	@Column(name = "sales_content", length = 500)
	private String salesContent; // 매출 관련 설명

	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	private LocalDateTime createdDate; // 매출 기록 생성 시간

	@UpdateTimestamp
	@Column(name = "updated_date")
	private LocalDateTime updatedDate; // 매출 기록 수정 시간

	public enum PaymentStatus {
		PENDING, // 결제 대기 중
		COMPLETED, // 결제 완료
		CANCELED // 결제 취소
	}

	public enum PaymentMethod {
		CREDIT_CARD, // 신용카드
		CASH, // 현금
		TRANSFER, // 계좌이체
		MOBILE // 모바일 결제
	}

	public enum SalesStatus {
		ACTIVE, // 정상 매출
		INACTIVE, // 비활성화된 매출
		REFUNDED // 환불된 매출
	}

	@Builder
	public Sales(SalesManagement salesManagement, Owner owner, Store store, Order order, String menuName, int price,
			int vat, int totalSales, PaymentStatus paymentStatus, PaymentMethod paymentMethod, SalesStatus salesStatus,
			String salesTitle, String salesContent) {
		this.salesManagement = salesManagement;
		this.owner = owner;
		this.store = store;
		this.order = order;
		this.menuName = menuName;
		this.price = price;
		this.vat = vat;
		this.totalSales = totalSales;
		this.paymentStatus = paymentStatus;
		this.paymentMethod = paymentMethod;
		this.salesStatus = salesStatus;
		this.salesTitle = salesTitle;
		this.salesContent = salesContent;
	}

}
