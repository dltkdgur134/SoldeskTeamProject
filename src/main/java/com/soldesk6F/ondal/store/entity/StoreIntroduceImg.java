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
	name = "store_introduce_img",
	uniqueConstraints = @UniqueConstraint(columnNames = {"store_id", "store_introduce_img"})
)
public class StoreIntroduceImg {

	@Id
	@GeneratedValue
	@org.hibernate.annotations.UuidGenerator
	@Column(name = "store_introduce_img_id", nullable = false, unique = true)
	private UUID storeIntroduceImgId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(name = "store_introduce_img", nullable = false, length = 255)
	private String storeIntroduceImg;

	public String getIntroduceImgIdAsString() {
		return storeIntroduceImgId != null ? storeIntroduceImgId.toString() : null;
	}
}



