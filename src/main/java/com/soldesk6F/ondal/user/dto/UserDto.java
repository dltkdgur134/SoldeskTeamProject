package com.soldesk6F.ondal.user.dto;

import com.soldesk6F.ondal.user.entity.User;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

// AdminUserController 용
@Getter
@Builder
public class UserDto {
	private UUID userUuid;
	private String userId;
	private String userName;
	private String nickName;
	private String email;
	private String userPhone;
	private String socialLoginProvider;
	private String userRole;
	private String userStatus;
	private String createdDate;

	public static UserDto from(User u) {
		return UserDto.builder()
				.userUuid(u.getUserUuid())
				.userId(u.getUserId())
				.userName(u.getUserName())
				.nickName(u.getNickName())
				.email(u.getEmail())
				.userPhone(u.getUserPhone())
				.socialLoginProvider(u.getSocialLoginProvider())
				.userRole(u.getUserRole().name())
				.userStatus(u.getUserStatus().name())
				.createdDate(u.getCreatedDate().toString())
				.build();
	}
}
