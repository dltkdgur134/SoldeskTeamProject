package com.soldesk6F.ondal.user.service;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.dto.rider.RiderForm;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.Rider.DeliveryRange;
import com.soldesk6F.ondal.user.entity.Rider.RiderStatus;
import com.soldesk6F.ondal.user.entity.User.UserRole;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.RiderRepository;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RiderService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RiderRepository riderRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    
    // 이미 라이더가 등록되어 있는지 확인하는 메서드
    public boolean isAlreadyRider(String userId) {
    	// RiderRepository에서 해당 userId에 해당하는 Rider가 존재하는지 확인
    	return riderRepository.existsByUser_UserId(userId);
    }

    public Rider getRiderByUserId(String userId) {
        return riderRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("라이더 정보가 존재하지 않습니다."));
    }
    
    
    public void registerRider(User user, RiderForm form) {
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
        Rider rider = Rider.builder()
                .user(user)
                .secondaryPassword(encodedSecondaryPassword) // 암호화된 비밀번호를 저장
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
    public void updateRiderSecondaryPassword(Rider rider, String newSecondaryPassword) {
        // 2차 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(newSecondaryPassword);
        
        // 암호화된 비밀번호를 Rider 객체에 저장
        rider.setSecondaryPassword(encodedPassword);
        
        // 저장된 비밀번호를 DB에 업데이트하는 로직 (예: repository.save(rider))
    }
    
    public boolean updateRiderSecondaryPassword(Rider rider, String currentSecondaryPassword, String newSecondaryPassword) {
        // 현재 비밀번호와 일치하는지 확인
        if (passwordEncoder.matches(currentSecondaryPassword, rider.getSecondaryPassword())) {
            // 2차 비밀번호가 일치하면 새 비밀번호로 업데이트
            String encodedPassword = passwordEncoder.encode(newSecondaryPassword);
            rider.setSecondaryPassword(encodedPassword);
            
            // DB에 업데이트 (예: riderRepository.save(rider))
            riderRepository.save(rider);
            
            return true; // 비밀번호 수정 성공
        } else {
            return false; // 비밀번호가 일치하지 않으면 수정 실패
        }
    }
    public void assignRiderToOrder(UUID orderId, UUID riderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        Rider rider = riderRepository.findById(riderId)
            .orElseThrow(() -> new RuntimeException("라이더를 찾을 수 없습니다."));

        // 주문에 라이더 배정
        order.setRider(rider);// rider를 Order에 설정

        orderRepository.save(order);  // 저장
    }
 // RiderStatus 변경 서비스
    public Rider changeRiderStatus(UUID riderId) {
        Rider rider = riderRepository.findById(riderId)
            .orElseThrow(() -> new RuntimeException("라이더를 찾을 수 없습니다"));

        rider.setRiderStatus(rider.getRiderStatus().next());
        return riderRepository.save(rider);
    }
    
    @Transactional
    public void completeOrderAndRewardRider(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));

        // 상태 검증
        if (order.getOrderToRider() != Order.OrderToRider.ON_DELIVERY) {
            throw new RuntimeException("배달 완료가 불가능한 상태입니다.");
        }

        Rider rider = order.getRider();
        if (rider == null) throw new RuntimeException("배정된 라이더가 없습니다.");

        // 배달 완료 시간 계산 및 저장
        LocalDateTime deliveryStartTime = order.getDeliveryStartTime();
        if (deliveryStartTime == null) {
            throw new RuntimeException("배달 시작 시간이 존재하지 않습니다.");
        }

        LocalDateTime startTime = order.getDeliveryStartTime();
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(startTime, now);
        LocalTime realDeliveryTime = LocalTime.ofSecondOfDay(duration.getSeconds());

        order.setRealDeliveryTime(realDeliveryTime);

        
        
        
        order.setOrderToRider(Order.OrderToRider.COMPLETED);
        orderRepository.save(order);

        // 라이더 보상 처리
        int deliveryFee = order.getDeliveryFee();
        if (deliveryFee <= 0) {
            throw new RuntimeException("유효하지 않은 배달료입니다.");
        }

        int newWalletAmount = rider.getRiderWallet() + deliveryFee;
        if (newWalletAmount < 0) {
            throw new RuntimeException("지갑 금액이 음수로 설정될 수 없습니다.");
        }

        rider.setRiderWallet(newWalletAmount);
        rider.setRiderStatus(Rider.RiderStatus.WAITING);
        riderRepository.save(rider);
    }

    
    
    
    
    
    
    
}
