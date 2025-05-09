package com.soldesk6F.ondal.rider.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.useract.order.entity.Order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "delivery_sales")
public class DeliverySales {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "delivery_sales_id", updatable = false, nullable = false, unique = true)
    private UUID deliverySalesId;  // 배달 매출 PK

    @ManyToOne
    @JoinColumn(name = "r_management_id", nullable = false)
    private RiderManagement riderManagement;  // 라이더의 매출과 연관 (N:1)

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;  // 배달 요청을 한 가게

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;  // 배달한 주문 (배달한 주문과 연결)

    @Column(name = "delivery_sales_date", nullable = false)
    private LocalDate deliverySalesDate;  // 배달이 완료된 날짜 (날짜 단위 정산 가능)


    @Column(name = "delivery_price", nullable = false)
    private int deliveryPrice;  // 배달료

    @Column(name = "delivery_vat", nullable = false)
    private int deliveryVat;  // 부가세 (배달료의 10%로 설정 가능)

    @Column(name = "rider_net_income",nullable = false)
    private int riderNetIncome; //실제 수익 (배달료에서 부가세를 뺀 값)
    
    
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus;  // 배달 상태 (배달 완료, 오배달, 배달 취소 등)

    public enum DeliveryStatus {
    	COMPLETED,  // 배달 완료
    	MISDELIVERED,  // 오배달
    	CANCELED  // 배달 취소
    }

    @Builder
	public DeliverySales(RiderManagement riderManagement, Store store, Order order, int deliveryPrice,
			int deliveryVat,int riderNetIncome, DeliveryStatus deliveryStatus) {
		super();
		this.riderManagement = riderManagement;
		this.store = store;
		this.order = order;
		this.deliveryPrice = deliveryPrice;
		this.deliveryVat = deliveryVat;
		this.riderNetIncome = riderNetIncome;
		this.deliveryStatus = deliveryStatus;
	}
    
    public String getDeliverySalesUuidAsString() {
	    return deliverySalesId != null ? deliverySalesId .toString() : null;
	}
    
    
}