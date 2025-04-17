package com.soldesk6F.ondal.user.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "rider")
public class Rider {

	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "rider_id", updatable = false, nullable = false)
	private UUID riderId;

	@OneToOne
	@JoinColumn(name = "user_uuid", nullable = false, unique = true)
	private User user;

	@Column(name = "rider_nickname" ,nullable = false , length = 30)
	private String riderNickname;
	
	@Column(name = "secondary_password", nullable = false, length = 255)
	private String secondaryPassword;

	@Column(name = "vehicle_number", nullable = false, length = 20)
	private String vehicleNumber;

	@Column(name = "rider_hub_address", nullable = false, length = 80)
	private String riderHubAddress;

	@Enumerated(EnumType.STRING)
	@Column(name = "delivery_range", nullable = false)
	private DeliveryRange deliveryRange;

	@Column(name = "rider_phone", nullable = false, length = 13)
	private String riderPhone;

	@Column(name = "hub_address_latitude", nullable = false)
	private double hubAddressLatitude;

	@Column(name = "hub_address_longitude", nullable = false)
	private double hubAddressLongitude;
	
	@Column(name = "rider_wallet",nullable = true )
    private int riderWallet;
	
	@CreationTimestamp
	@Column(name = "registration_date", updatable = false)
	private LocalDateTime registrationDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "rider_status", nullable = false ,length = 10)
	private RiderStatus riderStatus;

	public enum RiderStatus {
		WAITING("대기"), // 대기 (새로운 배달을 기다리는 상태)
		DELIVERING("배달 중"), // 배달 중
		RESTING("휴식 중"); // 휴식 중

		private final String description;

		RiderStatus(String description) {
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

	@PrePersist // rider는 기본적으로 대기 상태
	public void prePersist() {
		this.riderStatus = (this.riderStatus == null) ? RiderStatus.WAITING : this.riderStatus;
	}

	// Owner 생성자에 riderId와 registrationDate , riderStatus가 없는 이유:자동으로 생성하는 값이기에 없어도
	// 된다.
	
	// Owner 생성자에 riderId와 registrationDate , riderStatus가 없는 이유:자동으로 생성하는 값이기에 없어도
	// 된다.
	@Builder
	public Rider(User user, String secondaryPassword, String vehicleNumber, String riderHubAddress,
			DeliveryRange deliveryRange, String riderPhone, double hubAddressLatitude, double hubAddressLongitude,
			RiderStatus riderStatus,String riderNickname) {
		this.user = user;
		this.secondaryPassword = secondaryPassword;
		this.vehicleNumber = vehicleNumber;
		this.riderHubAddress = riderHubAddress;
		this.deliveryRange = deliveryRange;
		this.riderPhone = riderPhone;
		this.hubAddressLatitude = hubAddressLatitude;
		this.hubAddressLongitude = hubAddressLongitude;
		this.riderStatus = riderStatus;
		this.riderNickname = riderNickname;
	}

	public String getRiderUuidAsString() {
	    return riderId != null ? riderId .toString() : null;
	}
	

}
