package com.soldesk6F.ondal.menu.entity;

import com.soldesk6F.ondal.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "menu_category", uniqueConstraints = @UniqueConstraint(columnNames = {"store_id", "category_name"}))
public class MenuCategory {

	@Id
	@GeneratedValue
	@Column(name = "category_id")
	private UUID id;

	@Column(name = "category_name", nullable = false, length = 30)
	private String categoryName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Override
	public String toString() {
		return categoryName;
	}
	
	@Column(name = "category_order")
	private Integer order;
	
}