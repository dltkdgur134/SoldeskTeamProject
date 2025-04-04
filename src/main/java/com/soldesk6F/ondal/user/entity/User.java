package com.soldesk6F.ondal.user.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
	
	
	@Builder
	public User(String userId, String password, String userProfileName, String userProfileExtension,
			String userProfilePath, String userName, String nickName, String email, String userPhone,
			String userAddress, String socialLoginProvider) {
		super();
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
	}


}
