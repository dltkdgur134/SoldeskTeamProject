package com.soldesk6F.ondal.store.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UuidGenerator;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.soldesk6F.ondal.menu.entity.MenuCategory;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.Rider.DeliveryRange;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

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
	@JsonIgnoreProperties({"user"})
	private Owner owner;
	
	@Column(name = "business_num" , nullable = false , length = 10)
	private String businessNum;
	
	
	@Column(name = "store_name", nullable = false, length = 20)
    private String storeName;

	
    @Column(name = "category", nullable = false, length = 20)
    private String category;

    @Column(name = "store_phone", nullable = false, length = 13)
    private String storePhone;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<StoreImg> storeImgs = new ArrayList<>();
    
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreIntroduceImg> StoreIntroduceImgs = new ArrayList<>();
    
    @Column(name = "brand_img", length = 255)
    private String brandImg;

    @Column(name = "store_address", nullable = false, length = 80)
    private String storeAddress;

    @Column(name = "store_latitude", nullable = false)
    private double storeLatitude;

    @Column(name = "store_longitude", nullable = false)
    private double storeLongitude;
    
    @Column(name = "store_location",  columnDefinition = "POINT SRID 4326" , nullable = true)
    private Point storeLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_range")
    private DeliveryRange deliveryRange;

    @Lob
    @Column(name = "store_introduce")
    private String storeIntroduce;

    @Lob
    @Column(name = "store_event")
    private String storeEvent;

    @Lob
    @Column(name = "food_origin",nullable = false)
    private String foodOrigin;

    @JsonIgnore
    @Column(name = "opening_time")
    private LocalTime openingTime;

    @JsonIgnore
    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "holiday", length = 50)
    private String holiday;

    @Enumerated(EnumType.STRING)
    @Column(name = "store_status", nullable = false)
    private StoreStatus storeStatus;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "registration_date", updatable = false,nullable = false)
    private LocalDateTime registrationDate;
    
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)	 // 카테고리 정렬 순서 저장 컬럼
    @OrderBy("order ASC")
    private List<MenuCategory> menuCategories;

    public enum StoreStatus {
    	PENDING_APPROVAL("승인대기중"),
    	PENDING_REFUSES("승인거부"),
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
    
    public void addStoreIntroduceImg(StoreIntroduceImg img) {
    	img.setStore(this);
    	this.StoreIntroduceImgs.add(img);
    }

    
    @Builder
    public Store(Owner owner,String businessNum, String storeName, String category, String storePhone,
                 List<StoreImg> storeImgs,List<StoreIntroduceImg> StoreIntroduceImgs, String brandImg, String storeAddress,
                 double storeLatitude, double storeLongitude, Point storeLocation, DeliveryRange deliveryRange,
                 String storeIntroduce, String storeEvent , String foodOrigin,
                 LocalTime openingTime, LocalTime closingTime,
                 String holiday, StoreStatus storeStatus) {
        this.owner = owner;
        this.businessNum = businessNum;
        this.storeName = storeName;
        this.category = category;
        this.storePhone = storePhone;

        if (storeImgs != null) {
            storeImgs.forEach(this::addStoreImg);
        }
       
        if (StoreIntroduceImgs != null) {
        	StoreIntroduceImgs.forEach(this::addStoreIntroduceImg);
        }
        this.brandImg = brandImg;
        this.storeAddress = storeAddress;
        this.storeLatitude = storeLatitude;
        this.storeLongitude = storeLongitude;
        this.storeLocation = storeLocation;
        this.deliveryRange = deliveryRange;
        this.storeIntroduce = storeIntroduce;
        this.storeEvent = storeEvent;
        this.foodOrigin = foodOrigin;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.holiday = holiday;
        this.storeStatus = storeStatus != null ? storeStatus : StoreStatus.CLOSED;
    }
    
    public String getStoreUuidAsString() {
	    return storeId != null ? storeId .toString() : null;
	}
	
}
