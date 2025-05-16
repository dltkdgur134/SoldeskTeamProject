package com.soldesk6F.ondal.login;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.OwnerRepository;
import com.soldesk6F.ondal.user.repository.RiderRepository;
import com.soldesk6F.ondal.user.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository  userRepository;
    private final OwnerRepository ownerRepository;
    private final RiderRepository riderRepository;

    public CustomUserDetailsService(UserRepository  userRepository,
                                    OwnerRepository ownerRepository,
                                    RiderRepository riderRepository) {

        this.userRepository  = userRepository;
        this.ownerRepository = ownerRepository;
        this.riderRepository = riderRepository;
    }

    /**
     * 폼 로그인 / 기본 Security 인증에서 호출되는 메서드.
     * userId → User 조회 후 CustomUserDetails 로 래핑해 반환한다.
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        /* 필요 시, Owner·Rider 정보도 추가 조회 가능
        if (user.getUserRole() == UserRole.OWNER) {
            Owner owner = ownerRepository.findById(user.getUserUuid()).orElse(null);
            // owner 정보를 user 객체에 세팅하거나 attributes 맵에 담을 수도 있음
        }
        */

        // CustomUserDetails(User user, UserRole role) 사용
        return new CustomUserDetails(user);
    }
}
