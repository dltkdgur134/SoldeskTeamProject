package com.soldesk6F.ondal.store;

import java.time.LocalTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.user.entity.Owner;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "store")
public class Store {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "store_id" , updatable = false, nullable = false,unique = true)
	private UUID storeId;
	
	@ManyToOne
	@JoinColumn(name = "owner_id" , nullable = false)
	private Owner owner;
	
	@Column(name = "store_name", nullable = false, length = 20)
    private String storeName;

    @Column(name = "category", nullable = false, length = 20)
    private String category;

    @Column(name = "store_phone", nullable = false, length = 13)
    private String storePhone;

    @Column(name = "store_img_name", length = 255)
    private String storeImgName;

    @Column(name = "store_img_extension", length = 10)
    private String storeImgExtension;

    @Column(name = "store_img_path", length = 255)
    private String storeImgPath;

    @Column(name = "brand_img_name", length = 255)
    private String brandImgName;

    @Column(name = "brand_img_extension", length = 10)
    private String brandImgExtension;

    @Column(name = "brand_img_path", length = 255)
    private String brandImgPath;

    @Column(name = "store_address", nullable = false, length = 80)
    private String storeAddress;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "delivery_range", nullable = false)
    private double deliveryRange;

    @Lob
    @Column(name = "store_introduce")
    private String storeIntroduce;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Column(name = "holiday", length = 50)
    private String holiday;

    @Enumerated(EnumType.STRING)
    @Column(name = "store_status", nullable = false)
    private StoreStatus storeStatus;

    @CreationTimestamp
    @Column(name = "registration_date", updatable = false)
    private LocalTime registrationDate;

    public enum StoreStatus {
        OPEN,   // 영업 중
        CLOSED  // 영업 종료
    }

    @PrePersist
    public void prePersist() {
        this.storeStatus = (this.storeStatus == null) ? StoreStatus.CLOSED : this.storeStatus;
    }

	public Store(Owner owner, String storeName, String category, String storePhone, String storeImgName,
			String storeImgExtension, String storeImgPath, String brandImgName, String brandImgExtension,
			String brandImgPath, String storeAddress, double latitude, double longitude, double deliveryRange,
			String storeIntroduce, LocalTime openingTime, LocalTime closingTime, String holiday) {
		super();
		this.owner = owner;
		this.storeName = storeName;
		this.category = category;
		this.storePhone = storePhone;
		this.storeImgName = storeImgName;
		this.storeImgExtension = storeImgExtension;
		this.storeImgPath = storeImgPath;
		this.brandImgName = brandImgName;
		this.brandImgExtension = brandImgExtension;
		this.brandImgPath = brandImgPath;
		this.storeAddress = storeAddress;
		this.latitude = latitude;
		this.longitude = longitude;
		this.deliveryRange = deliveryRange;
		this.storeIntroduce = storeIntroduce;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
		this.holiday = holiday;
	}
	
	
}
