package com.soldesk6F.ondal.user.dto;


import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
	private UUID userUuid;
	private String userId;
	private String userProfile;
	private String nickName;
	private String email;
	private String userPhone;
	private String userRole;
	private String userStatus;
	
	public String getUserUuidAsString() {
		return userUuid != null ? userUuid.toString() : null;
	}
	
}
