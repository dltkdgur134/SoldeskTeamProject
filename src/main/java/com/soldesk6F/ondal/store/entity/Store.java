package com.soldesk6F.ondal.store.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.Rider.DeliveryRange;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
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
	
	@Column(name = "business_num" , nullable = false , length = 10)
	private String businessNum;
	
	
	@Column(name = "store_name", nullable = false, length = 20)
    private String storeName;

	
	
    @Column(name = "category", nullable = false, length = 20)
    private String category;

    @Column(name = "store_phone", nullable = false, length = 13)
    private String storePhone;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreImg> storeImgs = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BrandImg> brandImgs = new ArrayList<>();

    @Column(name = "store_address", nullable = false, length = 80)
    private String storeAddress;

    @Column(name = "store_latitude", nullable = false)
    private double storeLatitude;

    @Column(name = "store_longitude", nullable = false)
    private double storeLongitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_range")
    private DeliveryRange deliveryRange;

    @Lob
    @Column(name = "store_introduce")
    private String storeIntroduce;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "holiday", length = 50)
    private String holiday;

    @Enumerated(EnumType.STRING)
    @Column(name = "store_status", nullable = false)
    private StoreStatus storeStatus;

    @CreationTimestamp
    @Column(name = "registration_date", updatable = false)
    private LocalDateTime registrationDate;

    public enum StoreStatus {
        OPEN("영업중"),
        CLOSED("영업종료"),
        SUSPENDED("일시정지"),
        BANNED("영구정지");

        private final String description;

        StoreStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum DeliveryRange {
		ONE_KM(1), THREE_KM(3), FIVE_KM(5);

		private final int km;

		DeliveryRange(int km) {
			this.km = km;
		}

		public int getKm() {
			return km;
		}

		public static DeliveryRange fromKm(int km) {
			for (DeliveryRange range : values()) {
				if (range.km == km)
					return range;
			}
			throw new IllegalArgumentException("Invalid delivery range: " + km);
		}
	}
    public void addStoreImg(StoreImg img) {
        img.setStore(this);
        this.storeImgs.add(img);
    }

    public void addBrandImg(BrandImg img) {
        img.setStore(this);
        this.brandImgs.add(img);
    }
    
    @Builder
    public Store(Owner owner,String businessNum, String storeName, String category, String storePhone,
                 List<StoreImg> storeImgs, List<BrandImg> brandImgs, String storeAddress,
                 double storeLatitude, double storeLongitude, DeliveryRange deliveryRange,
                 String storeIntroduce, LocalTime openingTime, LocalTime closingTime,
                 String holiday, StoreStatus storeStatus) {
        this.owner = owner;
        this.businessNum = businessNum;
        this.storeName = storeName;
        this.category = category;
        this.storePhone = storePhone;

        // 이미지 리스트 추가
        if (storeImgs != null) {
            storeImgs.forEach(this::addStoreImg);
        }
        if (brandImgs != null) {
            brandImgs.forEach(this::addBrandImg);
        }

        this.storeAddress = storeAddress;
        this.storeLatitude = storeLatitude;
        this.storeLongitude = storeLongitude;
        this.deliveryRange = deliveryRange;
        this.storeIntroduce = storeIntroduce;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.holiday = holiday;
        this.storeStatus = storeStatus != null ? storeStatus : StoreStatus.CLOSED;
    }
    
    public String getStoreUuidAsString() {
	    return storeId != null ? storeId .toString() : null;
	}
	
	
}
