package com.soldesk6F.ondal.admin.controller;

import com.soldesk6F.ondal.user.dto.UserDto;
import com.soldesk6F.ondal.user.dto.UserUpdateRequest;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.User.UserRole;
import com.soldesk6F.ondal.user.entity.User.UserStatus;
import com.soldesk6F.ondal.user.repository.UserRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// 관리자 페이지 유저 설정
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

	private final UserRepository userRepository;

	@GetMapping("")
	public Page<UserDto> getUsers(
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "10") int size,
		@RequestParam(name = "role", defaultValue = "everything") String role,
		@RequestParam(name = "status", defaultValue = "everything") String status
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

		Specification<User> spec = (root, query, cb) -> {
			Predicate predicate = cb.conjunction();

			if (!"everything".equalsIgnoreCase(role)) {
				try {
					UserRole roleEnum = UserRole.valueOf(role.toUpperCase());
					predicate = cb.and(predicate, cb.equal(root.get("userRole"), roleEnum));
				} catch (IllegalArgumentException ignored) {
					// 예외 무시 → 전체 출력
				}
			}

			if (!"everything".equalsIgnoreCase(status)) {
				try {
					UserStatus statusEnum = UserStatus.valueOf(status.toUpperCase());
					predicate = cb.and(predicate, cb.equal(root.get("userStatus"), statusEnum));
				} catch (IllegalArgumentException ignored) {
					// 예외 무시 → 전체 출력
				}
			}

			return predicate;
		};

		return userRepository.findAll(spec, pageable).map(UserDto::from);
	}

	@GetMapping("/{uuid}")
	public UserDto getUser(@PathVariable("uuid") UUID uuid) {
		return UserDto.from(userRepository.findById(uuid).orElseThrow());
	}

	@PutMapping("/{uuid}")
	public ResponseEntity<?> updateUser(@PathVariable("uuid") UUID uuid, @RequestBody UserUpdateRequest req) {
		User user = userRepository.findById(uuid).orElseThrow();
		try {
			user.setNickName(req.getNickName());
			user.setUserRole(UserRole.valueOf(req.getUserRole()));
			user.setUserStatus(UserStatus.valueOf(req.getUserStatus()));
			userRepository.save(user);
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("역할 또는 상태 값이 잘못되었습니다.");
		}
	}
}
