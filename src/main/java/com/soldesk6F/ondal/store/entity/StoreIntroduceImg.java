package com.soldesk6F.ondal.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "store_introduce_img",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"store_id", "store_introduce_img"})
    }
)
public class StoreIntroduceImg {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    @Column(name = "store_introduce_img_id", nullable = false, unique = true)
    private UUID brandImgId;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    private String imgPath;

    @Column(name = "store_introduce_img", nullable = false, length = 255)
    private String storeIntroduceImg;

    @Builder
	public StoreIntroduceImg(Store store, String storeIntroduceImg) {
		super();
		this.store = store;
		this.storeIntroduceImg = storeIntroduceImg;
	}
    public String getBrandImgUuidAsString() {
	    return storeIntroduceImg != null ? storeIntroduceImg .toString() : null;
	}
}
