package com.soldesk6F.ondal.user.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.owner.wallet.repository.OwnerWalletHistoryRepository;
import com.soldesk6F.ondal.owner.wallet.service.OwnerWalletHistoryService;
import com.soldesk6F.ondal.user.dto.owner.OwnerForm;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.Rider.DeliveryRange;
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
	@Autowired
	private OwnerWalletHistoryRepository ownerWalletHistoryRepository;
	@Autowired
	private OwnerWalletHistoryService ownerWalletHistoryService;
	
	
	
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

        // Owner 객체 생성
        Owner owner = Owner.builder()
                .user(user)
                .ownerNickname(form.getOwnerNickname())
                .secondaryPassword(encodedSecondaryPassword) // 암호화된 비밀번호를 저장
                .build();
        ownerRepository.save(owner);  // Owner 저장
        
    }
    
    @Transactional
    public void updateOwnerInfo(String userId, String ownerNickname ) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("유저가 존재하지 않습니다.");
        }

        Optional<Owner> optionalOwner = ownerRepository.findByUser_UserId(userId);
        if (!optionalOwner.isPresent()) {
            throw new IllegalArgumentException("점주 정보가 존재하지 않습니다.");
        }

        Owner owner = optionalOwner.get();

        // 값이 null이 아닌 것만 수정
        if (ownerNickname != null) owner.setOwnerNickname(ownerNickname);
        ownerRepository.save(owner);
    }
    public void updateRiderSecondaryPassword(Owner owner, String newSecondaryPassword) {
        // 2차 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(newSecondaryPassword);
        
        // 암호화된 비밀번호를 Rider 객체에 저장
        owner.setSecondaryPassword(encodedPassword);
        
        // 저장된 비밀번호를 DB에 업데이트하는 로직 (예: repository.save(owner))
    }
    
    public boolean updateOwnerSecondaryPassword(Owner owner, String currentSecondaryPassword, String newSecondaryPassword) {
        // 현재 비밀번호와 일치하는지 확인
        if (passwordEncoder.matches(currentSecondaryPassword,owner.getSecondaryPassword())) {
            // 2차 비밀번호가 일치하면 새 비밀번호로 업데이트
            String encodedPassword = passwordEncoder.encode(newSecondaryPassword);
            owner.setSecondaryPassword(encodedPassword);
            
            // DB에 업데이트 (예: ownerRepository.save(owner))
            ownerRepository.save(owner);
            
            return true; // 비밀번호 수정 성공
        } else {
            return false; // 비밀번호가 일치하지 않으면 수정 실패
        }
    }
    public boolean checkOwnerSecondaryPassword(Owner owner, String currentSecondaryPassword) {

    	if (owner.isSecondaryPasswordLocked()) {
            // 잠금 상태인지 체크
            return false;
        }
    	// 최근 실패 시간과 현재 시간 차이로 리셋 여부 판단
    	if (owner.getLastSecondaryPasswordFailTime() != null &&
    	        Duration.between(owner.getLastSecondaryPasswordFailTime(), LocalDateTime.now()).toMinutes() > 10) {
    	        owner.setSecondaryPasswordFailCount(0); // 10분 지나면 초기화
    	    }

    	boolean isCorrect = passwordEncoder.matches(currentSecondaryPassword, owner.getSecondaryPassword());
    	
    	if (isCorrect) {
            owner.setSecondaryPasswordFailCount(0);
            owner.setSecondaryPasswordLocked(false);
            owner.setLastSecondaryPasswordFailTime(null);
        } else {
            owner.setSecondaryPasswordFailCount(owner.getSecondaryPasswordFailCount() + 1);
            owner.setLastSecondaryPasswordFailTime(LocalDateTime.now());

            if (owner.getSecondaryPasswordFailCount() >= 5) {
                owner.setSecondaryPasswordLocked(true); // 5회 실패 시 잠금
            }
        }
    	
    	ownerRepository.save(owner); // 상태 저장
        return isCorrect;
    	
    }
    @Transactional
    public String processWithdrawal(Owner owner, int withdrawAmount, String secondaryPassword) {
        // 1. 출금 금액이 10,000원 미만인 경우
        if (withdrawAmount < 10000) {
            return "최소 출금 금액은 10,000원입니다.";
        }
        if (withdrawAmount % 1000 != 0) {
        	return "출금은 1,000원 단위로만 가능합니다.";
        }
     // ✅ 3. 하루 3회 제한 (개발단계: 주석 처리)
        /*
        LocalDate today = LocalDate.now();
        int todayWithdrawCount = riderWalletHistoryService.countTodayWithdrawals(rider.getRiderId(), today);
        if (todayWithdrawCount >= 3) {
            return "출금은 하루 3회만 가능합니다.";
        }
        */
        
        // 2. 2차 비밀번호 검증
        boolean isSecondaryPasswordValid = checkOwnerSecondaryPassword(owner, secondaryPassword);
        if (!isSecondaryPasswordValid) {
            return "2차 비밀번호가 일치하지 않습니다.";
        }


        // 4. 수수료 계산 (출금 금액의 10%)
        int fee = withdrawAmount / 10;  // 10% 수수료
        int actualAmount = withdrawAmount + fee;
        // 출금 금액 검증 (잔액보다 많은 금액을 출금할 수 없음)
        if (actualAmount > owner.getOwnerWallet()) {
        	return "잔액이 부족합니다.";
        }

        // 5. 출금 처리 (잔액에서 출금 금액 차감)
        owner.setOwnerWallet(owner.getOwnerWallet() - actualAmount);
        ownerRepository.save(owner);  // 변경된 정보 저장

        // 6. 출금 내역 기록 (출금 기록을 DB에 저장)
        ownerWalletHistoryService.saveWalletHistory(owner, withdrawAmount, fee, actualAmount, "출금 요청");

        // 7. 성공 메시지 반환
        return String.format("출금 성공! %d원이 출금되었습니다. (수수료 %d원)", actualAmount, fee);
    }
    @Transactional
    public String convertOwnerWalletToOndalWallet(Owner owner, int amount, String secondaryPassword) {
        // 1. 유효성 검증
    	if (amount < 10000) {
            return "최소 출금 금액은 10,000원입니다.";
        }
    	
    	if (amount % 1000 != 0) {
            return "전환은 1,000원 단위로만 가능합니다.";
        }

        if (!checkOwnerSecondaryPassword(owner, secondaryPassword)) {
            return "2차 비밀번호가 일치하지 않습니다.";
        }

        if (owner.getOwnerWallet() < amount) {
            return "잔액이 부족합니다.";
        }

        // 2. 수수료 계산 및 실 전환 금액
        int fee = amount / 10; // 10% 수수료
        int actualAmount = amount + fee;
     
        //  출금 금액 검증 (잔액보다 많은 금액을 출금할 수 없음)
        if (actualAmount > owner.getOwnerWallet()) {
        	return "잔액이 부족합니다.";
        }

        // 3. 금액 차감 및 ondalWallet 증가
        owner.setOwnerWallet(owner.getOwnerWallet() - actualAmount);
        owner.getUser().setOndalWallet(owner.getUser().getOndalWallet() + amount);

        // 4. 저장
        ownerRepository.save(owner);
        userRepository.save(owner.getUser());

        // 5. 이력 저장
        ownerWalletHistoryService.saveWalletHistory(
            owner, amount, fee, amount, "온달 지갑 전환"
        );

        return String.format("전환 성공! %d원이 전환되었습니다. (수수료 %d원)", amount, fee);
    }

    
    
    public int countTodayWithdrawals(UUID ownerId, LocalDate today) {
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        return ownerWalletHistoryRepository.countByOwner_OwnerIdAndCreatedDateBetween(
        		ownerId, startOfDay, endOfDay);
    }
    
}
