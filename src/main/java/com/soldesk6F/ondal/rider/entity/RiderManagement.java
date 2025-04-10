package com.soldesk6F.ondal.rider.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.user.entity.Rider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "rider_management")
public class RiderManagement {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "rider_management_id", updatable = false, nullable = false, unique = true)
    private UUID riderManagementId;  // 배달 관리 PK
    
    @OneToOne
    @JoinColumn(name = "rider_id", nullable = false, unique = true)
    private Rider rider;  // 라이더 1명당 1개의 배달 관리 정보 (1:1 관계)

    @Column(name = "total_vat", nullable = false)
    private int totalVat;  // 총 부가세

    @Column(name = "total_sales", nullable = false)
    private int totalSales;  // 총 매출액
    
    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;  // 최초 매출 발생 시점

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;  // 매출이 갱신될 때마다 업데이트

 // 총 매출액과 부가세를 업데이트하는 메서드
    public void updateTotalSalesAndVat(int deliverySales, int deliveryVat) {
        if (deliverySales < 0 || deliveryVat < 0) {
            throw new IllegalArgumentException("매출액과 부가세는 0 이상이어야 합니다.");
        }
        this.totalSales += deliverySales;
        this.totalVat += deliveryVat;
    }
    @Builder
	public RiderManagement(Rider rider, int totalVat, int totalSales) {
		super();
		this.rider = rider;
		this.totalVat = totalVat;
		this.totalSales = totalSales;
	}
    public String getRiderManagementUuidAsString() {
	    return riderManagementId != null ? riderManagementId .toString() : null;
	}
    
}
