package com.soldesk6F.ondal.user.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="user")
public class User {
	@Id
	@Column(name = "user_id", nullable = false ,length = 15)
	private String userId;
	
	@Column(name = "password",nullable = false,length = 255)
	private String password;
	
	@Column(name = "user_profile_name",nullable = false,length = 255)
	private String userProfileName;
	
	@Column(name = "user_profile_extension",nullable = false,length = 10)
	private String userProfileExtension;
	
	@Column(name = "user_profile_path",nullable = false,length = 255)
	private String userProfilePath;
	
	@Column(name = "user_name",nullable = false,length = 10)
	private String userName;
	
	@Column(name = "nickname",nullable = false,length = 20)
	private String nickName;
	
	@Column(name = "email",nullable = false,length = 30)
	private String email;
	
	@Column(name = "user_phone",nullable = false,length = 13)
	private String userPhone;
	
	@Column(name = "user_address",nullable = false,length = 80)
	private String userAddress;
	
	@Column(name = "social_login_provider",nullable = false,length = 30)
	private String socialLoginProvider;
	
	@CreationTimestamp
	@Column(name = "created_date",nullable = false, updatable = false)
	private LocalDateTime createdDate;
	
	@UpdateTimestamp
	@Column(name = "updated_date",nullable = false)
	private LocalDateTime updatedDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "user_status", nullable = false, length = 20)
	private UserStatus userStatus = UserStatus.UNVERIFIED;
	
	public enum UserStatus {
	    ACTIVE("정상"),      // 정상 회원
	    SUSPENDED("일시 정지"),   // 일시 정지
	    BANNED("영구 정지"),      // 영구 정지
	    UNVERIFIED("미 인증");   // 이메일 인증 미완료 (기본값)
	    private final String description;

	    UserStatus(String description) {
	        this.description = description;
	    }

	    public String getDescription() {
	        return description;
	    }
	}
	@Builder
    public User(String userId, String password, String userProfileName, String userProfileExtension,
                String userProfilePath, String userName, String nickName, String email, 
                String userPhone, String userAddress, String socialLoginProvider, 
                UserStatus userStatus) {
        this.userId = userId;
        this.password = password;
        this.userProfileName = userProfileName;
        this.userProfileExtension = userProfileExtension;
        this.userProfilePath = userProfilePath;
        this.userName = userName;
        this.nickName = nickName;
        this.email = email;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        this.socialLoginProvider = (socialLoginProvider == null || socialLoginProvider.isBlank()) ? "NONE" : socialLoginProvider;
        this.userStatus = (userStatus != null) ? userStatus : UserStatus.UNVERIFIED;
    }

}
