package com.soldesk6F.ondal.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "store_img",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"store_id", "store_img_file_path"})
    }
)
public class StoreImg {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    @Column(name = "store_img_id", nullable = false, unique = true)
    private UUID storeImgId;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;


    @Column(name = "store_img_file_path", nullable = false, length = 255)
    private String StoreImgFilePath;

    @Builder
	public StoreImg(Store store, String storeImgFilePath) {
		super();
		this.store = store;
		StoreImgFilePath = storeImgFilePath;
	}
    
    public String getStoreImgUuidAsString() {
	    return storeImgId != null ? storeImgId .toString() : null;
	}
    
}
