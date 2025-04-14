package com.soldesk6F.ondal.user.service;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.dto.RiderForm;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.Rider.DeliveryRange;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.RiderRepository;
import com.soldesk6F.ondal.user.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RiderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RiderRepository riderRepository;


    // 이미 라이더가 등록되어 있는지 확인하는 메서드
    public boolean isAlreadyRider(String userId) {
    	// RiderRepository에서 해당 userId에 해당하는 Rider가 존재하는지 확인
    	return riderRepository.existsByUser_UserId(userId);
    }

    public Rider getRiderByUserId(String userId) {
        return riderRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("라이더 정보가 존재하지 않습니다."));
    }
    
    
    public void registerRider(User user ,RiderForm form) {
        // 현재 인증된 사용자 가져오기
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // userId로 User 객체 찾기
        String userId = customUserDetails.getUsername();  // customUserDetails.getUser().getUserId()와 동일
        Optional<User> optionalUser = userRepository.findByUserId(userId);  // userId로 User 객체를 찾아옴
        
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("유저가 존재하지 않습니다.");
        }
        
        user = optionalUser.get();  // 존재하는 경우 User 객체 가져오기

        // Rider 객체 생성
        Rider rider = Rider.builder()
                .user(user)
                .secondaryPassword(form.getSecondaryPassword())
                .vehicleNumber(form.getVehicleNumber())
                .riderHubAddress(form.getRiderHubAddress())
                .riderPhone(form.getRiderPhone())
                .hubAddressLatitude(form.getHubAddressLatitude())
                .hubAddressLongitude(form.getHubAddressLongitude())
                .riderNickname(form.getRiderNickname())
                .build();

        // DeliveryRange 값 설정 (Enum 변환)
        try {
            DeliveryRange deliveryRange = DeliveryRange.valueOf(form.getDeliveryRange());
            rider.setDeliveryRange(deliveryRange);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("배달 반경이 올바르지 않습니다.");
        }

        riderRepository.save(rider);  // Rider 저장
    }

    @Transactional
    public void updateRiderInfo(String userId, String riderNickname,String vehicleNumber, 
    							String riderPhone, String riderHubAddress,
    							double hubAddressLatitude,double hubAddressLongitude,
    							DeliveryRange deliveryRange) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("유저가 존재하지 않습니다.");
        }

        Optional<Rider> optionalRider = riderRepository.findByUser_UserId(userId);
        if (!optionalRider.isPresent()) {
            throw new IllegalArgumentException("라이더 정보가 존재하지 않습니다.");
        }

        Rider rider = optionalRider.get();

        // 값이 null이 아닌 것만 수정
        if (riderNickname != null) rider.setRiderNickname(riderNickname);
        if (vehicleNumber != null) rider.setVehicleNumber(vehicleNumber);
        if (riderHubAddress != null) rider.setRiderHubAddress(riderHubAddress);
        if (riderPhone != null) rider.setRiderPhone(riderPhone);
        if (hubAddressLatitude > 0) rider.setHubAddressLatitude(hubAddressLatitude);
        if (hubAddressLongitude > 0) rider.setHubAddressLongitude(hubAddressLongitude);
        if (deliveryRange != null) rider.setDeliveryRange(deliveryRange);
        riderRepository.save(rider);
    }

}
