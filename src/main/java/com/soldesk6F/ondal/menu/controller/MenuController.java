package com.soldesk6F.ondal.menu.controller;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.menu.dto.MenuDto;
import com.soldesk6F.ondal.menu.dto.MenuOrderDto;
import com.soldesk6F.ondal.menu.dto.MenuRegisterDto;
import com.soldesk6F.ondal.menu.service.MenuService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

	private final MenuService menuService;

	// ✅ 메뉴 상세 조회
	@GetMapping("/{menuId}")
	public ResponseEntity<MenuDto> getMenuDetail(@PathVariable UUID menuId) {
		MenuDto dto = menuService.getMenuDetail(menuId);
		return ResponseEntity.ok(dto);
	}
	
	@PostMapping("/menu-reorder")
	@ResponseBody
	public ResponseEntity<?> reorderMenu(@RequestBody List<MenuOrderDto> updates) {
		try {
			menuService.updateMenuOrder(updates);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	

	
}

