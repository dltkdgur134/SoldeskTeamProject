package com.soldesk6F.ondal.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "brand_img",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"store_id", "brand_img"})
    }
)
public class BrandImg {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    @Column(name = "brand_img_id", nullable = false, unique = true)
    private UUID brandImgId;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;


    @Column(name = "brand_img", nullable = false, length = 255)
    private String brandImg;

    @Builder
	public BrandImg(Store store, String brandImg) {
		super();
		this.store = store;
		this.brandImg = brandImg;
	}
    public String getBrandImgUuidAsString() {
	    return brandImgId != null ? brandImgId .toString() : null;
	}
}
