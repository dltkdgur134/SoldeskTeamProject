package com.soldesk6F.ondal.user.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateBasicDTO {
	private String userId;
	private String userProfile;
	private String nickName;
	private String email;
	private String userPhone;
	
}
