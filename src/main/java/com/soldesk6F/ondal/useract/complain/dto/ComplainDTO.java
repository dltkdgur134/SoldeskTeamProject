package com.soldesk6F.ondal.useract.complain.dto;

import java.util.UUID;

import com.soldesk6F.ondal.useract.complain.entity.Complain.Role;

import lombok.Data;

@Data
public class ComplainDTO {
	private UUID complainId;  // 저장된 후 업데이트 용도
    private String complainTitle;
    private String complainContent;
    private Role role;                    
    private String complainPassword;      
}
