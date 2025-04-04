package com.soldesk6F.ondal.user.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="user")
public class User {

    @Id
    @Column(name = "user_id", updatable = false,nullable = false, length = 15,unique = true)
    private String userId;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "user_profile_name", nullable = false, length = 255)
    private String userProfileName;

    @Column(name = "user_profile_extension", nullable = false, length = 10)
    private String userProfileExtension;

    @Column(name = "user_profile_path", nullable = false, length = 255)
    private String userProfilePath;

    @Column(name = "user_name", nullable = false, length = 10)
    private String userName;

    @Column(name = "nickname", nullable = false, length = 20)
    private String nickName;

    @Column(name = "email", nullable = false, length = 30)
    private String email;

    @Column(name = "user_phone", nullable = false, length = 13)
    private String userPhone;

    @Column(name = "user_address", nullable = false, length = 80)
    private String userAddress;

    @Column(name = "social_login_provider", nullable = false, length = 30)
    private String socialLoginProvider;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @CreationTimestamp
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    //@PrePersist에서 기본값을 정해서 DB에 저장
    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false, length = 20)
    private Status status;
    
    public enum Status {
    	 UNVERIFIED, 	//미 인증 상태
    	 ACTIVE, 		// 활동 중(정상)
    	 SUSPENDED, 	// 경고
    	 BANNED;		// 영구 정지
    }
    
    @PrePersist		// user는 회원가입시 이메일 인증 전이기에 기본적으로 미 인증 상태
    public void prePersist() {
        this.status = (this.status == null) ? Status.UNVERIFIED : this.status;
    }
    
    
    @Builder
    public User(String userId, String password, String userProfileName, String userProfileExtension,
                String userProfilePath, String userName, String nickName, String email, String userPhone,
                String userAddress, String socialLoginProvider) {
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
        this.socialLoginProvider = socialLoginProvider;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}