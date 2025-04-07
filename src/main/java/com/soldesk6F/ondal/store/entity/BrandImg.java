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
        @UniqueConstraint(columnNames = {"store_id", "brand_img_file_name"})
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

    @Column(name = "brand_img_file_name", nullable = false, length = 255)
    private String brandImgFileName;

    @Column(name = "brand_img_file_extension", nullable = false, length = 10)
    private String brandImgFileExtension;

    @Column(name = "brand_img_file_path", nullable = false, length = 255)
    private String brandImgFilePath;

    @Builder
	public BrandImg(Store store, String brandImgFileName, String brandImgFileExtension, String brandImgFilePath) {
		super();
		this.store = store;
		this.brandImgFileName = brandImgFileName;
		this.brandImgFileExtension = brandImgFileExtension;
		this.brandImgFilePath = brandImgFilePath;
	}

}
