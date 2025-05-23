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

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.rider.entity.DeliverySales;
import com.soldesk6F.ondal.rider.entity.RiderManagement;
import com.soldesk6F.ondal.rider.repository.DeliverySalesRepository;
import com.soldesk6F.ondal.rider.repository.RiderManagementRepository;
import com.soldesk6F.ondal.rider.repository.RiderWalletHistoryRepository;
import com.soldesk6F.ondal.rider.service.RiderWalletHistoryService;
import com.soldesk6F.ondal.user.dto.rider.RiderForm;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.Rider.DeliveryRange;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.RiderRepository;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class RiderService {

	private final RiderWalletHistoryRepository riderWalletHistoryRepository;

	private final UserRepository userRepository;
	private final RiderRepository riderRepository;
	private final OrderRepository orderRepository;
	private final RiderManagementRepository riderManagementRepository;
	private final DeliverySalesRepository deliverySalesRepository;
	private final PasswordEncoder passwordEncoder;
	private final RiderWalletHistoryService riderWalletHistoryService; // RiderWalletHistoryService 주입

	@Autowired
	public RiderService(UserRepository userRepository, RiderRepository riderRepository, OrderRepository orderRepository,
			PasswordEncoder passwordEncoder, RiderWalletHistoryService riderWalletHistoryService,
			RiderWalletHistoryRepository riderWalletHistoryRepository,
			RiderManagementRepository riderManagementRepository,DeliverySalesRepository deliverySalesRepository) {

		this.userRepository = userRepository;
		this.riderRepository = riderRepository;
		this.orderRepository = orderRepository;
		this.passwordEncoder = passwordEncoder;
		this.riderWalletHistoryService = riderWalletHistoryService;
		this.riderWalletHistoryRepository = riderWalletHistoryRepository; // 생성자 주입
		this.riderManagementRepository = riderManagementRepository;
		this.deliverySalesRepository = deliverySalesRepository;
	}

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
		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		// userId로 User 객체 찾기
		String userId = customUserDetails.getUsername(); // customUserDetails.getUser().getUserId()와 동일
		Optional<User> optionalUser = userRepository.findByUserId(userId); // userId로 User 객체를 찾아옴

		if (!optionalUser.isPresent()) {
			throw new IllegalArgumentException("유저가 존재하지 않습니다.");
		}

		user = optionalUser.get(); // 존재하는 경우 User 객체 가져오기

		// secondaryPassword 암호화
		String encodedSecondaryPassword = passwordEncoder.encode(form.getSecondaryPassword());
		// Rider 객체 생성
		Rider rider = Rider.builder().user(user).secondaryPassword(encodedSecondaryPassword) // 암호화된 비밀번호를 저장
				.vehicleNumber(form.getVehicleNumber()).riderHubAddress(form.getRiderHubAddress())
				.riderPhone(form.getRiderPhone()).hubAddressLatitude(form.getHubAddressLatitude())
				.hubAddressLongitude(form.getHubAddressLongitude()).riderNickname(form.getRiderNickname()).build();
		// DeliveryRange 값 설정 (Enum 변환)
		try {
			DeliveryRange deliveryRange = DeliveryRange.valueOf(form.getDeliveryRange());
			rider.setDeliveryRange(deliveryRange);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("배달 반경이 올바르지 않습니다.");
		}
		riderRepository.save(rider); // Rider 저장

	}

	
	
	@Transactional
	public void updateRiderInfo(String userId, String riderNickname, String vehicleNumber, String riderPhone,
			String riderHubAddress, double hubAddressLatitude, double hubAddressLongitude,
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
		if (riderNickname != null)
			rider.setRiderNickname(riderNickname);
		if (vehicleNumber != null)
			rider.setVehicleNumber(vehicleNumber);
		if (riderHubAddress != null)
			rider.setRiderHubAddress(riderHubAddress);
		if (riderPhone != null)
			rider.setRiderPhone(riderPhone);
		if (hubAddressLatitude > 0)
			rider.setHubAddressLatitude(hubAddressLatitude);
		if (hubAddressLongitude > 0)
			rider.setHubAddressLongitude(hubAddressLongitude);
		if (deliveryRange != null)
			rider.setDeliveryRange(deliveryRange);
		riderRepository.save(rider);
	}

	public void updateRiderSecondaryPassword(Rider rider, String newSecondaryPassword) {
		// 2차 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(newSecondaryPassword);

		// 암호화된 비밀번호를 Rider 객체에 저장
		rider.setSecondaryPassword(encodedPassword);

		// 저장된 비밀번호를 DB에 업데이트하는 로직 (예: repository.save(rider))
	}

	public boolean updateRiderSecondaryPassword(Rider rider, String currentSecondaryPassword,
			String newSecondaryPassword) {
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

	public boolean checkRiderSecondaryPassword(Rider rider, String currentSecondaryPassword) {

		if (rider.isSecondaryPasswordLocked()) {
			// 잠금 상태인지 체크
			return false;
		}
		// 최근 실패 시간과 현재 시간 차이로 리셋 여부 판단
		if (rider.getLastSecondaryPasswordFailTime() != null
				&& Duration.between(rider.getLastSecondaryPasswordFailTime(), LocalDateTime.now()).toMinutes() > 10) {
			rider.setSecondaryPasswordFailCount(0); // 10분 지나면 초기화
		}

		boolean isCorrect = passwordEncoder.matches(currentSecondaryPassword, rider.getSecondaryPassword());

		if (isCorrect) {
			rider.setSecondaryPasswordFailCount(0);
			rider.setSecondaryPasswordLocked(false);
			rider.setLastSecondaryPasswordFailTime(null);
		} else {
			rider.setSecondaryPasswordFailCount(rider.getSecondaryPasswordFailCount() + 1);
			rider.setLastSecondaryPasswordFailTime(LocalDateTime.now());

			if (rider.getSecondaryPasswordFailCount() >= 5) {
				rider.setSecondaryPasswordLocked(true); // 5회 실패 시 잠금
			}
		}

		riderRepository.save(rider); // 상태 저장
		return isCorrect;

	}

	// 주문에 라이더 배정하기
	public void assignRiderToOrder(UUID orderId, UUID riderId) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

		Rider rider = riderRepository.findById(riderId).orElseThrow(() -> new RuntimeException("라이더를 찾을 수 없습니다."));

		// 주문에 라이더 배정
		order.setRider(rider);// rider를 Order에 설정

		orderRepository.save(order); // 저장
	}

	// RiderStatus 변경 서비스 (프론트에서 버튼 클릭으로 대기 <-> 휴식)
	public Rider changeRiderStatus(UUID riderId) {
		Rider rider = riderRepository.findById(riderId).orElseThrow(() -> new RuntimeException("라이더를 찾을 수 없습니다"));

		rider.setRiderStatus(rider.getRiderStatus().next());
		return riderRepository.save(rider);
	}

	// OrderToRider , OrderToUser 변경 및 배달료 riderWallet에 입금 (OrderToRider = COMPLETED)
	@Transactional
	public void completeOrderAndRewardRider(UUID orderId) {
	    // 1. Order 정보 가져오기
	    Order order = orderRepository.findById(orderId)
	        .orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));

	    // 상태 검증
	    if (order.getOrderToRider() != Order.OrderToRider.ON_DELIVERY) {
	        throw new RuntimeException("배달 완료가 불가능한 상태입니다.");
	    }

	    Rider rider = order.getRider();
	    if (rider == null) throw new RuntimeException("배정된 라이더가 없습니다.");

	    // 2. RiderManagement 가져오기
	    RiderManagement riderManagement = riderManagementRepository.findByRider(rider)
	        .orElseThrow(() -> new RuntimeException("라이더 관리 정보가 존재하지 않습니다."));

	    // 3. 배달 완료 시간 계산 및 저장
	    LocalDateTime deliveryStartTime = order.getDeliveryStartTime();
	    if (deliveryStartTime == null) {
	        throw new RuntimeException("배달 시작 시간이 존재하지 않습니다.");
	    }

	    LocalDateTime now = LocalDateTime.now();
	    Duration duration = Duration.between(deliveryStartTime, now);
	    LocalTime realDeliveryTime = LocalTime.ofSecondOfDay(duration.getSeconds());

	    order.setRealDeliveryTime(realDeliveryTime);
	    order.setOrderToRider(Order.OrderToRider.COMPLETED);
	    order.setOrderToUser(Order.OrderToUser.COMPLETED);
	    orderRepository.save(order);

	    // 4. 배달료 및 수수료 계산
	    int deliveryFee = order.getDeliveryFee();
	    int deliveryPrice = deliveryFee;  // 라이더 매출액
	    int vat = (int) (deliveryFee * 0.1);  // 예시: 10%가 수수료
	    int riderNetIncome = deliveryFee - vat; // 실제 라이더 수익

	    // 5. DeliverySales 생성 및 저장
	    DeliverySales deliverySales = new DeliverySales();
	    deliverySales.setOrder(order);
	    deliverySales.setStore(order.getStore());
	    deliverySales.setDeliveryPrice(deliveryPrice);
	    deliverySales.setDeliveryVat(vat);
	    deliverySales.setRiderNetIncome(riderNetIncome);
	    deliverySales.setDeliverySalesDate(LocalDate.now());
	    deliverySales.setDeliveryStatus(DeliverySales.DeliveryStatus.COMPLETED);
	    deliverySales.setRiderManagement(riderManagement);
	    deliverySalesRepository.save(deliverySales);

	    // 6. 라이더 보상 처리: riderWallet 업데이트
	    int newWalletAmount = rider.getRiderWallet() + riderNetIncome;
	    if (newWalletAmount < 0) {
	        throw new RuntimeException("지갑 금액이 음수로 설정될 수 없습니다.");
	    }

	    rider.setRiderWallet(newWalletAmount);
	    rider.setRiderStatus(Rider.RiderStatus.WAITING);  // 상태 업데이트 (배달 완료 후)
	    riderRepository.save(rider);

	    // 7. RiderManagement 업데이트: 총 매출 및 수수료 업데이트
	    riderManagement.updateTotalSalesAndVat(deliveryPrice, vat);
	    riderManagementRepository.save(riderManagement);
	}

	//출금 서비스 (현금화)
	@Transactional
	public String processWithdrawal(Rider rider, int withdrawAmount, String secondaryPassword) {
		// 1. 출금 금액이 10,000원 미만인 경우
		if (withdrawAmount < 10000) {
			return "최소 출금 금액은 10,000원입니다.";
		}
		if (withdrawAmount % 1000 != 0) {
			return "출금은 1,000원 단위로만 가능합니다.";
		}
		// ✅ 3. 하루 3회 제한 (개발단계: 주석 처리)
		/*
		 * LocalDate today = LocalDate.now(); int todayWithdrawCount =
		 * riderWalletHistoryService.countTodayWithdrawals(rider.getRiderId(), today);
		 * if (todayWithdrawCount >= 3) { return "출금은 하루 3회만 가능합니다."; }
		 */

		// 2. 2차 비밀번호 검증
		boolean isSecondaryPasswordValid = checkRiderSecondaryPassword(rider, secondaryPassword);
		if (!isSecondaryPasswordValid) {
			return "2차 비밀번호가 일치하지 않습니다.";
		}


		// 3. 수수료 계산 (출금 금액의 10%)
		int fee = withdrawAmount / 10; // 10% 수수료
		int actualAmount = withdrawAmount + fee;
		// 출금 금액 검증 (잔액보다 많은 금액을 출금할 수 없음)
		if (actualAmount > rider.getRiderWallet()) {
			return "잔액이 부족합니다.";
		}

		// 4. 출금 처리 (잔액에서 출금 금액 차감)
		rider.setRiderWallet(rider.getRiderWallet() - actualAmount);
		riderRepository.save(rider); // 변경된 정보 저장

		// 5. 출금 내역 기록 (출금 기록을 DB에 저장)
		riderWalletHistoryService.saveWalletHistory(rider, withdrawAmount, fee, actualAmount, "출금 요청");

		// 6. 성공 메시지 반환
		return String.format("출금 성공! %d원이 출금되었습니다. (수수료 %d원)", actualAmount, fee);
	}

	// riderWallet -> ondalWallet
	@Transactional
	public String convertRiderWalletToOndalWallet(Rider rider, int amount, String secondaryPassword) {
		if (amount < 10000) {
            return "최소 출금 금액은 10,000원입니다.";
        }
    	
    	if (amount % 1000 != 0) {
            return "전환은 1,000원 단위로만 가능합니다.";
        }

	    if (!checkRiderSecondaryPassword(rider, secondaryPassword)) {
	        return "2차 비밀번호가 일치하지 않습니다.";
	    }


	    int fee = amount / 10;
	    int actualAmount = amount - fee;
	    if (actualAmount > rider.getRiderWallet()) {
	    	return "잔액이 부족합니다.";
	    }

	    rider.setRiderWallet(rider.getRiderWallet() - actualAmount);
	    rider.getUser().setOndalWallet(rider.getUser().getOndalWallet() + amount);

	    riderRepository.save(rider);
	    userRepository.save(rider.getUser());

	    riderWalletHistoryService.saveWalletHistory(
	        rider, amount, fee, amount, "온달 지갑 전환"
	    );

	    return String.format("전환 성공! %d원이 전환되었습니다. (수수료 %d원)", amount, fee);
	}
	
	public int countTodayWithdrawals(UUID riderId, LocalDate today) {
		LocalDateTime startOfDay = today.atStartOfDay();
		LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
		return riderWalletHistoryRepository.countByRider_RiderIdAndCreatedDateBetween(riderId, startOfDay, endOfDay);
	}

}
