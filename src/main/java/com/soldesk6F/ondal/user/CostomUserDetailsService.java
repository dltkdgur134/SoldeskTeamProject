package com.soldesk6F.ondal.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.soldesk6F.ondal.config.WebServerConfig;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.OwnerRepository;
import com.soldesk6F.ondal.user.repository.RiderRepository;
import com.soldesk6F.ondal.user.repository.UserRepository;
import java.util.UUID;

@Service
public class CostomUserDetailsService implements UserDetailsService {
	
	    private final UserRepository userRepository;
	    private final OwnerRepository onwerRepository;
	    private final RiderRepository riderRepository;
	    
    public CostomUserDetailsService(UserRepository userRepository , OwnerRepository ownerRepository,
    		RiderRepository riderRepository) {
        this.userRepository = userRepository;
        this.onwerRepository = ownerRepository;
        this.riderRepository = riderRepository;
        
        
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    	UUID userUUID = UUID.fromString(userId);
        User user = userRepository.findById(userUUID)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        
        if(onwerRepository.existsByUser_UserId(userId) || riderRepository.existsByUser_UserId(userId)) {
        if(onwerRepository.existsByUser_UserId(userId) && riderRepository.existsByUser_UserId(userId)) {
        	return new CustomUserDetails(user , Role.ALL);
        	}else {
        		return onwerRepository.existsByUser_UserId(userId) ? new CustomUserDetails(user , Role.OWNER) : new CustomUserDetails(user,Role.RIDER);
        	}
        }	
       return new CustomUserDetails(user,Role.USER);	
        
    }
    
    
    
    
}
	
	
	
		
	
	
	
	
	
	
	
	

