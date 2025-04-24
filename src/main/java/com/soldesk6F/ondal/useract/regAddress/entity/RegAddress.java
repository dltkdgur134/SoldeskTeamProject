package com.soldesk6F.ondal.useract.regAddress.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "reg_address")
public class RegAddress {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "reg_address_id", nullable = false , unique = true)
	private UUID regAddressId;
	
	@ManyToOne
	@JoinColumn(name = "user_uuid", nullable = false)
	private User user;
	
	@Column(name ="address" , nullable =  false)
	private String address;
	
	@Column(name = "detail_address" , nullable = false)
	private String detailAddress;
	
	
	@Column(name = "user_address_latitude", nullable = false)
	private double userAddressLatitude;

	@Column(name = "user_address_longitude", nullable = false)
	private double userAddressLongitude;
	
	@CreationTimestamp
	@Column(name = "created_date" , nullable = false , updatable = false)
	private LocalDateTime createdDate;
	
	@UpdateTimestamp
	@Column(name = "updated_date", nullable = false)
	private LocalDateTime updatedDate;
	
	@Column(name = "is_user_selected_address", nullable = false)
	private boolean isUserSelectedAddress;

	
	@Builder
	public RegAddress(User user, String address,String detailAddress, double userAddressLatitude, 
			double userAddressLongitude, boolean isUserSelectedAddress) {
		super();
		this.user = user;
		this.address = address;
		this.detailAddress = detailAddress;
		this.userAddressLatitude = userAddressLatitude;
		this.userAddressLongitude = userAddressLongitude;
		this.isUserSelectedAddress = isUserSelectedAddress;
	}
	
	public RegAddress updateDefaultAddress(boolean isUserSelectedAddress) {
		this.isUserSelectedAddress = isUserSelectedAddress;
		return this;
	}
	
	public String getUserUuidAsString() {
	    return regAddressId != null ? regAddressId .toString() : null;
	}




	
	
	
}
