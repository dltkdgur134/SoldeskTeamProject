package com.soldesk6F.ondal.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.soldesk6F.ondal.config.SecurityConfig;
import com.soldesk6F.ondal.config.WebServerConfig;

@Service
public class CostomUserDetailsService implements UserDetailsService {
	
	    private final UserRepository userRepository;

    public CostomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        
        
        
        return new user CustomUserDetails(user);	
    
    }
    
    
    
    
}
	
	
	
		
	
	
	
	
	
	
	
	

