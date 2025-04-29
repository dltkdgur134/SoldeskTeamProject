package com.soldesk6F.ondal.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
	name = "store_img",
	uniqueConstraints = @UniqueConstraint(columnNames = {"store_id", "store_img"})
)
public class StoreImg {

	@Id
	@GeneratedValue
	@org.hibernate.annotations.UuidGenerator
	@Column(name = "store_img_id", nullable = false, unique = true)
	private UUID storeImgId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(name = "store_img", nullable = false, length = 255)
	private String storeImg;

	public String getStoreImgIdAsString() {
		return storeImgId != null ? storeImgId.toString() : null;
	}
}


