package com.soldesk6F.ondal.user;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomUserDetails implements UserDetails , OAuth2User {
    private final User user;
    private Map<String, Object> attributes;
    private Role role;
    
    
    public CustomUserDetails(User user ,  Role role) {
        this.user = user;
        this.role = role;
        
    }
    
    public CustomUserDetails(User user, Map<String, Object> attributes,Role role) {
        this.user = user;
        this.attributes = attributes;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getKey()));
    }
    
    @Override
    public String getPassword() {	
        return user.getPassword();
    }
    

    
    @Override
    public String getUsername() {
        return user.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
        return attributes != null ? attributes.get("sub").toString() : user.getUserId();
	}
}