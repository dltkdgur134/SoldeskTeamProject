package com.soldesk6F.ondal.owner.sales.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.Owner;

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
@Table(name = "sales_management")
public class SalesManagement {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "s_management_id", nullable = false, unique = true)
	private UUID sManagementId;

	@OneToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private Owner owner;

	@OneToOne
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(name = "total_vat", nullable = false)
	private int totalVat; // 총 부가세

	@Column(name = "total_sales", nullable = false)
	private int totalSales; // 총 매출액

	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	private LocalDateTime createdDate; // 최초 매출 발생 시점

	@UpdateTimestamp
	@Column(name = "updated_date")
	private LocalDateTime updatedDate; // 매출이 갱신될 때마다 업데이트

	// 총 매출액과 부가세를 업데이트하는 메서드
	public void updateTotalSalesAndVat(int StoreSales, int StoreVat) {
		if (StoreSales < 0 || StoreVat < 0) {
			throw new IllegalArgumentException("매출액과 부가세는 0 이상이어야 합니다.");
		}
		this.totalSales += StoreSales;
		this.totalVat += StoreVat;
	}

	@Builder
	public SalesManagement(Owner owner, Store store, int totalVat, int totalSales) {
		super();
		this.owner = owner;
		this.store = store;
		this.totalVat = totalVat;
		this.totalSales = totalSales;
	}

}
