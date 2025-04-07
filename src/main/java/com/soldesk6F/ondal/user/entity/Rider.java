package com.soldesk6F.ondal.user.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.rider.entity.DeliverySales;
import com.soldesk6F.ondal.rider.entity.RiderManagement;
import com.soldesk6F.ondal.rider.entity.DeliverySales.DeliveryStatus;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.useract.order.entity.Order;

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
@Table(name= "rider")
public class Rider {
	
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name= "rider_id", updatable = false, nullable = false, unique = true)
	private UUID riderId;
	
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;
	
	@Column(name = "secondary_password", nullable = false ,length=10)
	private String secondaryPassword;
	
	@Column(name = "vehicle_number" , nullable = false , length = 15)
	private String vehicleNumber;
	
	@Column(name = "rider_hub_address" , nullable = false, length = 80)
	private String riderHubAddress;
	
	@Column(name = "rider_phone" , nullable = false, length = 13)
	private String riderPhone;
	
	
	@Column(name = "latitude",nullable = false)
	private double latitude;
	
	@Column(name = "longitude",nullable = false)
	private double longitude;
	
	@CreationTimestamp
    @Column(name = "registration_date", updatable = false)
    private LocalDateTime registrationDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "rider_status", nullable = false)
	private RiderStatus riderStatus;
	
	public enum RiderStatus {
	    WAITING("대기"),       // 대기 (새로운 배달을 기다리는 상태)
	    DELIVERING("배달 중"),    // 배달 중
	    RESTING("휴식 중");        // 휴식 중

	    private final String description;

	    RiderStatus(String description) {
	        this.description = description;
	    }

	    public String getDescription() {
	        return description;
	    }
	}

	// Owner 생성자에 riderId와 registrationDate , riderStatus가 없는 이유:자동으로 생성하는 값이기에 없어도 된다.
	@Builder
	public Rider(User user, String secondaryPassword, String vehicleNumber, String riderHubAddress, String riderPhone,
			double latitude, double longitude, RiderStatus riderStatus) {
		super();
		this.user = user;
		this.secondaryPassword = secondaryPassword;
		this.vehicleNumber = vehicleNumber;
		this.riderHubAddress = riderHubAddress;
		this.riderPhone = riderPhone;
		this.latitude = latitude;
		this.longitude = longitude;
		this.riderStatus = (this.riderStatus == null) ? RiderStatus.WAITING : this.riderStatus;
	}
	
	
	
	
}
