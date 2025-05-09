package com.soldesk6F.ondal.login;

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
public class CustomUserDetailsService implements UserDetailsService {
	
	    private final UserRepository userRepository;
	    private final OwnerRepository onwerRepository;
	    private final RiderRepository riderRepository;
	    
    public CustomUserDetailsService(UserRepository userRepository , OwnerRepository ownerRepository,
    		RiderRepository riderRepository) {
        this.userRepository = userRepository;
        this.onwerRepository = ownerRepository;
        this.riderRepository = riderRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        return new CustomUserDetails(user, user.getUserRole());
    }
}