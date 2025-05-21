package com.soldesk6F.ondal.admin.controller;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.User.UserRole;
import com.soldesk6F.ondal.user.dto.AdminUserApprovalDto;
import com.soldesk6F.ondal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// 관리자 페이지 점주/라이더 승인 페이지
@RestController
@RequestMapping("/api/admin/requests")
@RequiredArgsConstructor
public class AdminUserApprovalController {

	private final UserRepository userRepository;

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
		} else if ("rider".equalsIgnoreCase(type)) {
			user.setRiderRequested(false);
			if (user.getUserRole() == UserRole.USER) {
				user.setUserRole(UserRole.RIDER);
			} else if (user.getUserRole() == UserRole.OWNER) {
				user.setUserRole(UserRole.ALL);
			}
		}

		userRepository.save(user);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/reject/{uuid}/{type}")
	public ResponseEntity<?> reject(@PathVariable("uuid") UUID uuid, @PathVariable("type") String type) {
		User user = userRepository.findByUserUuid(uuid).orElseThrow();
		if ("owner".equalsIgnoreCase(type)) {
			user.setOwnerRequested(false);
		} else if ("rider".equalsIgnoreCase(type)) {
			user.setRiderRequested(false);
		}
		userRepository.save(user);
		return ResponseEntity.ok().build();
	}
}
