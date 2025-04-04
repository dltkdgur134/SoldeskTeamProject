package com.soldesk6F.ondal.rider.ridermanagement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.order.entity.Order;
import com.soldesk6F.ondal.store.entity.Store;

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


    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus;  // 배달 상태 (배달 완료, 오배달, 배달 취소 등)

    public enum DeliveryStatus {
    	COMPLETED,  // 배달 완료
    	MISDELIVERED,  // 오배달
    	CANCELED  // 배달 취소
    }
    
    @Column(name = "delivery_sales_title", length = 30)
    private String deliverySalesTitle;  // 라이더가 입력할 수 있는 제목 (선택 사항)
    
    @Lob
    @Column(name = "delivery_content")
    private String deliveryContent;  // 라이더가 입력할 수 있는 설명 (선택 사항)

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;  // 매출 기록 생성 시간

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;  // 매출 기록 수정 시간

    @PrePersist
    @PreUpdate
    public void updateTotalSales() {
        if (this.deliveryStatus == DeliveryStatus.COMPLETED) {
            // 배달이 완료된 경우에만 반영
            int deliverySales = this.deliveryPrice - this.deliveryVat;

            // 부가세가 배달료보다 크거나 부정적인 값이 되지 않도록 검증
            if (deliverySales < 0) {
                throw new IllegalArgumentException("배달 매출액은 부가세를 빼면 0 이상이어야 합니다.");
            }

            RiderManagement riderManagement = this.riderManagement;
            riderManagement.setTotalSales(riderManagement.getTotalSales() + deliverySales);

            // RiderManagement 업데이트 필요 (예시: EntityManager 또는 Repository 사용)
            // riderManagementRepository.save(riderManagement);  // 예시 저장 작업
        }
    }
    
    @Builder
	public DeliverySales(RiderManagement riderManagement, Store store, Order order, int deliveryPrice,
			int deliveryVat, DeliveryStatus deliveryStatus, String deliverySalesTitle, String deliveryContent) {
		super();
		this.riderManagement = riderManagement;
		this.store = store;
		this.order = order;
		this.deliveryPrice = deliveryPrice;
		this.deliveryVat = deliveryVat;
		this.deliveryStatus = deliveryStatus;
		this.deliverySalesTitle = deliverySalesTitle;
		this.deliveryContent = deliveryContent;
	}
    
    
    
    
}