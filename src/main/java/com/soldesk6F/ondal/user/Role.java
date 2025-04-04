package com.soldesk6F.ondal.user;

import lombok.Getter;
@Getter
public enum Role {

	
	
	USER("ROLE_USER"),
	STORE("ROLE_STORE"),
	RIDER("ROLE_RIDER"),
	ALL("ROLE_ALL");
	
	private final String key;
	
    Role(String key) {
        this.key = key;
    }
	
	
	
}
