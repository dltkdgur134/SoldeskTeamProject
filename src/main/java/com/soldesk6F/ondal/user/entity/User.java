package com.soldesk6F.ondal.user.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

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

	@Column(name = "password", nullable = false, length = 255)
	private String password;

	@Column(name = "user_profile", length = 255)
	private String userProfile;

	@Column(name = "user_name", nullable = false, length = 15)
	private String userName;

	@Column(name = "nickname", nullable = false, length = 30)
	private String nickName;

	@Column(name = "email", nullable = false, unique = true, length = 50)
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
	private boolean userProfileLiveUpdate =false;

	@Column(name = "login_fail_count", nullable = false)
	private int loginFailCount = 0;
	
	@CreationTimestamp
	@Column(name = "created_date", nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@UpdateTimestamp
	@Column(name = "updated_date", nullable = false)
	private LocalDateTime updatedDate;
	
	
	
	public User update(String nickName , String userProfile) {
		this.nickName = nickName;
		this.userProfile = userProfile;
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
	

	@Enumerated(EnumType.STRING)
	@Column(name = "user_status", nullable = false, length = 20)
	private UserStatus userStatus = UserStatus.UNLINKED;

	
	public enum UserStatus {
		ACTIVE("정상"), // 정상 회원
		SUSPENDED("일시 정지"), // 일시 정지
		BANNED("영구 정지"), // 영구 정지
		UNLINKED("미연동");	// 소셜 미연동 상태
		

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
	
	public String getUserUuidAsString() {
	    return userUuid != null ? userUuid.toString() : null;
	}
	
	
}
