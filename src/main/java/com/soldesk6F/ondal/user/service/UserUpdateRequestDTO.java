//package com.soldesk6F.ondal.user.service;
//
//import java.util.Date;
//
//import org.springframework.security.core.userdetails.User;
//
//import com.soldesk6F.ondal.user.entity.User.UserRole;
//import com.soldesk6F.ondal.user.entity.User.UserStatus;
//
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
//
//@Getter
//@NoArgsConstructor
//public class UserUpdateRequestDTO {
//	
//	private String userId;
//	private String password;
//	private String userProfilePath;
//	private String userName;
//	private String nickName;
//	private String email;
//	private String userPhone;
//	private String userAddress;
//	private String socialLoginProvider;
//	private Date createdDate;
//	private Date updatedDate;
//	private UserRole userRole = UserRole.USER;
//	private UserStatus userStatus = UserStatus.UNVERIFIED;
//	
//	
//	@Builder
//	public UserUpdateRequestDTO(String userId, String password, String userProfileName, String userProfileExtension,
//            String userProfilePath, String userName, String nickName, String email, 
//            String userPhone, String userAddress, String socialLoginProvider,UserRole userRole,
//            UserStatus userStatus) {
//		super();
//		this.userId = userId;
//        this.password = password;
//        this.userProfilePath = userProfilePath;
//        this.userName = userName;
//        this.nickName = nickName;
//        this.email = email;
//        this.userPhone = userPhone;
//        this.userAddress = userAddress;
//        this.socialLoginProvider = (socialLoginProvider == null || socialLoginProvider.isBlank()) ? "NONE" : socialLoginProvider;
//        this.userRole = (userRole != null) ? userRole : UserRole.USER;
//        this.userStatus = (userStatus != null) ? userStatus : UserStatus.UNVERIFIED;
//	}
//	
//	public User toEntity() {
//		return User.builder()
//				.userId(userId)
//				.password(password)
//				.userProfilePath(userProfilePath)
//				.userName(userName)
//				.nickName(nickName)
//				.email(email)
//				.userPhone(userPhone)
//				.userAddress(userAddress)
//				.socialLoginProvider(socialLoginProvider)
//				.userRole(userRole)
//				.userStatus(userStatus)
//				.build();
//	}
//	
//	
//	
//}
