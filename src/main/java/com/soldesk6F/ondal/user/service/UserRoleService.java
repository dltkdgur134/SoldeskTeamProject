package com.soldesk6F.ondal.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.user.dto.owner.OwnerForm;
import com.soldesk6F.ondal.user.dto.rider.RiderForm;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.User.UserRole;
import com.soldesk6F.ondal.user.repository.UserRepository;

@Service
public class UserRoleService {
    private final OwnerService ownerService;
    private final RiderService riderService;
    private final UserRepository userRepository;

    @Autowired
    public UserRoleService(OwnerService ownerService, RiderService riderService,
    		UserRepository userRepository) {
        this.ownerService = ownerService;
        this.riderService = riderService;
        this.userRepository = userRepository;
    }

    public void changeRoleToOwner(User user, OwnerForm form) {
        if (riderService.isAlreadyRider(user.getUserId())) {
            user.setUserRole(UserRole.ALL);  // 라이더와 점주 역할 동시에 부여
        } else {
            user.setUserRole(UserRole.OWNER);  // 점주 역할만 부여
        }
        userRepository.save(user);
        // 기타 점주 관련 로직 처리
        ownerService.registerOwner(user, form); // Owner 등록
    }

    public void changeRoleToRider(User user, RiderForm form) {
        if (ownerService.isAlreadyOwner(user.getUserId())) {
            user.setUserRole(UserRole.ALL);  // 점주와 라이더 역할 동시에 부여
        } else {
            user.setUserRole(UserRole.RIDER);  // 라이더 역할만 부여
        }
        
        userRepository.save(user);
        // 기타 라이더 관련 로직 처리
        riderService.registerRider(user, form); // 라이더 등록 로직
    }
}

