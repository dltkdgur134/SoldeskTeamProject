package com.soldesk6F.ondal.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdatePasswordRequest {
	private String userId;
	private String password;
	private String oldPassword;
	private String newPassword;
	
}
