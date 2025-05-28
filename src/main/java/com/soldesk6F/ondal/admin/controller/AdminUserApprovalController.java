package com.soldesk6F.ondal.admin.controller;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.User.UserRole;
import com.soldesk6F.ondal.rider.repository.RiderManagementRepository;
import com.soldesk6F.ondal.user.dto.AdminUserApprovalDto;
import com.soldesk6F.ondal.user.repository.OwnerRepository;
import com.soldesk6F.ondal.user.repository.RiderRepository;
import com.soldesk6F.ondal.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// 관리자 페이지 점주/라이더 승인 페이지
@Slf4j
@RestController
@RequestMapping("/api/admin/requests")
@RequiredArgsConstructor
public class AdminUserApprovalController {

	private final UserRepository userRepository;
	private final OwnerRepository ownerRepository;
	private final RiderRepository riderRepository;
	private final RiderManagementRepository riderManagementRepository;


	@GetMapping("/owner")
	public List<AdminUserApprovalDto> getOwnerRequests() {
		return userRepository.findAll().stream()
			.filter(User::isOwnerRequested)
			.filter(user -> {
				if (user.getUserRole() == UserRole.OWNER || user.getUserRole() == UserRole.ALL) {
					user.setOwnerRequested(false);
					userRepository.save(user);
					return false;
				}
				return true;
			})
			.map(AdminUserApprovalDto::from)
			.collect(Collectors.toList());
	}

	@GetMapping("/rider")
	public List<AdminUserApprovalDto> getRiderRequests() {
		return userRepository.findAll().stream()
			.filter(User::isRiderRequested)
			.filter(user -> {
				if (user.getUserRole() == UserRole.RIDER || user.getUserRole() == UserRole.ALL) {
					user.setRiderRequested(false);
					userRepository.save(user);
					return false;
				}
				return true;
			})
			.map(AdminUserApprovalDto::from)
			.collect(Collectors.toList());
	}

	// 요청 승인시
	@Transactional
	@PostMapping("/approve/{uuid}/{type}")
	public ResponseEntity<?> approve(@PathVariable("uuid") UUID uuid, @PathVariable("type") String type) {
		User user = userRepository.findByUserUuid(uuid).orElseThrow(() ->
		new IllegalArgumentException("error - 해당 UUID에 해당하는 유저가 없습니다: " + uuid));

		if ("owner".equalsIgnoreCase(type)) {
			user.setOwnerRequested(false);
			if (user.getUserRole() == UserRole.USER) {
				user.setUserRole(UserRole.OWNER);
			} else if (user.getUserRole() == UserRole.RIDER) {
				user.setUserRole(UserRole.ALL);
			}
			log.info("관리자에 의해 OWNER 권한이 승인됨 - userId: {}", user.getUserId());
		} else if ("rider".equalsIgnoreCase(type)) {
			user.setRiderRequested(false);
			if (user.getUserRole() == UserRole.USER) {
				user.setUserRole(UserRole.RIDER);
			} else if (user.getUserRole() == UserRole.OWNER) {
				user.setUserRole(UserRole.ALL);
			}
			log.info("관리자에 의해 RIDER 권한이 승인됨 - userId: {}", user.getUserId());
		}

		userRepository.save(user);
		return ResponseEntity.ok().build();
	}

	// 요청 거절시
	@Transactional
	@PostMapping("/reject/{uuid}/{type}")
	public ResponseEntity<?> reject(@PathVariable("uuid") UUID uuid, @PathVariable("type") String type) {
		try {
			User user = userRepository.findByUserUuid(uuid).orElseThrow(() -> new EntityNotFoundException("User not found"));
			
			// 라이더/점주 요청 거부시 신청 값을 false로 바꾸고 uuid에 연결된 라이더 혹은 점주 데이터 삭제 
			switch (type.toLowerCase()) {
				case "owner" -> {
					user.setOwnerRequested(false);
					
					ownerRepository.findByUser(user).ifPresent(owner -> {
						user.setOwner(null); // user에서 owner와 관계 끊기
						owner.setUser(null); // owner에서도 user와 객체 관계 끊기
						/* ownerRepository.save(owner); */ // 관계 끊은 상태로 저장
						ownerRepository.delete(owner); // owner 정보 삭제
					});
					log.info("관리자에 의해 OWNER 신청이 거절됨 - userId: {}", user.getUserId());
				}
				case "rider" -> {
					user.setRiderRequested(false);
					riderRepository.findByUser(user).ifPresent(rider -> {
						// ✅ RiderManagement 먼저 삭제
						riderManagementRepository.findByRider(rider).ifPresent(rm -> {
							riderManagementRepository.delete(rm);
						});
						user.setRider(null);
						rider.setUser(null);
						/* riderRepository.save(rider); */
						riderRepository.delete(rider);
					});
					log.info("관리자에 의해 RIDER 신청이 거절됨 - userId: {}", user.getUserId());
				}
				// 이상한 입력으로 인한 에러 방지
				default -> {
					return ResponseEntity.badRequest().body("Invalid type: must be 'owner' or 'rider'");
				}
			}
			userRepository.save(user);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("❌ 거절 처리 중 서버 오류 발생: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
		}
	}
}
