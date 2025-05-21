package com.soldesk6F.ondal.user.dto;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.User.UserRole;
import com.soldesk6F.ondal.user.entity.User.UserStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

// AdminUserApprovalController 용
public record AdminUserApprovalDto(
	UUID userUuid,
	String userId,
	String userName,
	String nickName,
	UserRole userRole,
	UserStatus userStatus, 
	String createdDate // LocalDateTime → String
) {
	public static AdminUserApprovalDto from(User user) {
		return new AdminUserApprovalDto(
			user.getUserUuid(),
			user.getUserId(),
			user.getUserName(),
			user.getNickName(),
			user.getUserRole(),
			user.getUserStatus(),
			user.getCreatedDate().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm"))
		);
	}
}

