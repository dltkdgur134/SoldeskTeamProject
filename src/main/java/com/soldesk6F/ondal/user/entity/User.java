package com.soldesk6F.ondal.user.entity;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soldesk6F.ondal.useract.regAddress.entity.RegAddress;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "user_profile" }) })
public class User {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "user_uuid", nullable = false, unique = true)
	private UUID userUuid;

	@Column(name = "user_id", nullable = false, length = 50)
	private String userId;
	
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
	private Rider rider;

	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
	private Owner owner;
	
	@Column(name = "password", nullable = false, length = 255)
	private String password;

	@Column(name = "user_profile", length = 255)
	private String userProfile;

	@Column(name = "user_name", nullable = false, length = 15)
	private String userName;

	@Column(name = "nickname", nullable = false, length = 30)
	private String nickName;

	@Column(name = "email", nullable = false, length = 50)
	private String email;

	@Column(name = "user_phone", nullable = false, length = 13)
	private String userPhone;

	@ManyToOne
	@JoinColumn(name = "user_selected_address", nullable = true)
	private RegAddress userSelectedAddress;
	
	@Column(name = "social_login_provider", nullable = false, length = 30)
	private String socialLoginProvider;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_role", nullable = false, length = 20)
	private UserRole userRole = UserRole.USER;

	@Column(name = "user_profile_live_update", nullable = false)
	private boolean userProfileLiveUpdate = false;

	@Column(name = "login_fail_count", nullable = false)
	private int loginFailCount = 0;

	@JsonIgnore
	@CreationTimestamp
	@Column(name = "created_date", nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@Column(name = "ondal_wallet",nullable = true)
    private int ondalWallet;
	
	@Column(name = "ondal_pay",nullable = true)
	private int ondalPay;

	@JsonIgnore
	@UpdateTimestamp
	@Column(name = "updated_date", nullable = false)
	private LocalDateTime updatedDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "user_status", nullable = false, length = 20)
	private UserStatus userStatus = UserStatus.UNLINKED;
    
    @Column(name = "owner_requested", nullable = false)
    private boolean ownerRequested = false;

    @Column(name = "rider_requested", nullable = false)
    private boolean riderRequested = false;

	public enum UserStatus {
		ACTIVE("정상"), // 정상 회원
		SUSPENDED("일시 정지"), // 일시 정지
		BANNED("영구 정지"), // 영구 정지
		UNLINKED("미연동"), // 소셜 미연동 상태
		LEAVED("탈퇴"),	// 회원 탈퇴 상태(1년간 보관)
		DORMANCY("휴면 상태"); //장기간 비활동 상태

		private final String description;

		UserStatus(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	public enum UserRole {
		USER("유저"), OWNER("점주"), RIDER("배달원"), ALL("모든");
		
		private final String description;
		
		UserRole(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
	}
	
	
	
	
	@Builder
	public User(String userId, String password, String userProfile, String userName, String nickName, String email,
			String userPhone, RegAddress userSelectedAddress, String socialLoginProvider, UserRole userRole,
			UserStatus userStatus, boolean userProfileLiveUpdate) {
		this.userId = userId;
		this.password = password;
		this.userProfile = userProfile;
		this.userName = userName;
		this.nickName = nickName;
		this.email = email;
		this.userPhone = userPhone;
		this.userSelectedAddress = userSelectedAddress;
		this.socialLoginProvider = (socialLoginProvider == null || socialLoginProvider.isBlank()) ? "NONE"
				: socialLoginProvider;
		this.userRole = (userRole != null) ? userRole : UserRole.USER;
		this.userStatus = (userStatus != null) ? userStatus : UserStatus.UNLINKED;
		this.userProfileLiveUpdate = userProfileLiveUpdate;
	}

	
	public User update(String name) { 
		this.userName = name; 
		return this; 
	}
	 
	public User update(String nickName, String userProfilePath) {
		this.nickName = nickName;
		this.userProfile = userProfilePath;
		return this;
	}
	
	public User updateNickname(String nickName) {
		this.nickName = nickName;
		return this;
	}
	
	public User updateProfile(String userProfile) {
		this.userProfile = userProfile;
		return this;
	}
	
	public User updatePhone(String userPhone) {
		this.userPhone = userPhone;
		return this;
	}
	
	public User updateProvider(String socialLoginProvider) {
		this.socialLoginProvider = socialLoginProvider;
		return this;
	}

	public User updateEmail(String email) {
		this.email = email;
		return this;
	}
	
	public User updatePassword(String password) {
		this.password = password;
		return this;
	}
	
	public User updateUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
		return this;
	}
	
	public User updateUserSelectedAddress(RegAddress userSelectedAddress) {
		this.userSelectedAddress = userSelectedAddress;
		return this;
	}
	
	public String getUserUuidAsString() {
		return userUuid != null ? userUuid.toString() : null;
	}
}
