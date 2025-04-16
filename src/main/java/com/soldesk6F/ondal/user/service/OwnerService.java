package com.soldesk6F.ondal.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.user.entity.Owner;
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
	
	// 이미 점주가 등록되어 있는지 확인하는 메서드
    public boolean isAlreadyOwner(String userId) {
    	// RiderRepository에서 해당 userId에 해당하는 Rider가 존재하는지 확인
    	return ownerRepository.existsByUser_UserId(userId);
    }
	
    public Owner getOwnerByUserId(String userId) {
        return ownerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("점주 정보가 존재하지 않습니다."));
    }
    
    
    
}
