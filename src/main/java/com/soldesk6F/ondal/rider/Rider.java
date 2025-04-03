package com.soldesk6F.ondal.rider;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
	
	@Column(name = "latitude",nullable = false)
	private double latitude;
	
	@Column(name = "longitude",nullable = false)
	private double longitude;
	
	@CreationTimestamp
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "rider_status", nullable = false)
	private RiderStatus riderStatus;
	
	public enum RiderStatus {
	    WAITING,       // 대기 (새로운 배달을 기다리는 상태)
	    DELIVERING,    // 배달 중
	    RESTING        // 휴식 중
	}
	@PrePersist
    public void prePersist() {
        this.riderStatus = (this.riderStatus == null) ? RiderStatus.WAITING : this.riderStatus;
    }
	
	// Owner 생성자에 riderId와 registrationDate , riderStatus가 없는 이유:자동으로 생성하는 값이기에 없어도 된다.
	public Rider(User userId, String secondart_Password, String vehicleNumber, String riderHubAddress, double latitude,
			double longitude) {
		super();
		this.user = userId;
		this.secondaryPassword = secondart_Password;
		this.vehicleNumber = vehicleNumber;
		this.riderHubAddress = riderHubAddress;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	
}
