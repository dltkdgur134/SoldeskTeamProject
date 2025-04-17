package com.soldesk6F.ondal.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.dto.OwnerForm;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.OwnerRepository;
import com.soldesk6F.ondal.user.repository.UserRepository;

@Service
public class OwnerService {
	@Autowired
    private UserRepository userRepository;
	@Autowired
	private OwnerRepository ownerRepository;
	@Autowired
    private PasswordEncoder passwordEncoder;
	// 이미 점주가 등록되어 있는지 확인
    public boolean isAlreadyOwner(String userId) {
    	// RiderRepository에서 해당 userId에 해당하는 Rider가 존재하는지 확인
    	return ownerRepository.existsByUser_UserId(userId);
    }
	
    public Owner getOwnerByUserId(String userId) {
        return ownerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("점주 정보가 존재하지 않습니다."));
    }
    
    @Transactional
    public void registerOwner(User user, OwnerForm form) {
        // 현재 인증된 사용자 가져오기
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // userId로 User 객체 찾기
        String userId = customUserDetails.getUsername();  // customUserDetails.getUser().getUserId()와 동일
        Optional<User> optionalUser = userRepository.findByUserId(userId);  // userId로 User 객체를 찾아옴
        
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("유저가 존재하지 않습니다.");
        }
        
        user = optionalUser.get();  // 존재하는 경우 User 객체 가져오기

        // secondaryPassword 암호화
        String encodedSecondaryPassword = passwordEncoder.encode(form.getSecondaryPassword());

        // Rider 객체 생성
        Owner owner = Owner.builder()
                .user(user)
                .ownerNickname(form.getOwnerNickname())
                .secondaryPassword(encodedSecondaryPassword) // 암호화된 비밀번호를 저장
                .build();
        ownerRepository.save(owner);  // Owner 저장
        
    }
}
