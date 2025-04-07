package com.soldesk6F.ondal.user;

import lombok.Getter;
@Getter
public enum Role {

	
	
	USER("ROLE_USER"),
	OWNER("ROLE_OWNER"),
	RIDER("ROLE_RIDER"),
	ALL("ROLE_ALL");
	
	private final String key;
	
    Role(String key) {
        this.key = key;
    }
	
	
	
}
