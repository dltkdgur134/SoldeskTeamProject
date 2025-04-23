package com.soldesk6F.ondal.menu.controller;

import com.soldesk6F.ondal.menu.dto.MenuCategoryDto;
import com.soldesk6F.ondal.menu.entity.MenuCategory;
import com.soldesk6F.ondal.menu.service.MenuCategoryService;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.soldesk6F.ondal.login.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class MenuCategoryController {

	private final MenuCategoryService menuCategoryService;
	private final StoreService storeService; // 로그인된 사용자에게 Store 연결이 필요하다면 사용

	@PostMapping("/add")
	public MenuCategoryDto addCategory(@RequestParam("name") String name, @RequestParam("storeId") UUID storeId, @AuthenticationPrincipal CustomUserDetails userDetails) {
		System.out.println("💡 카테고리 추가 요청: " + name + ", storeId: " + storeId);
		Store store = storeService.getStoreForOwner(storeId, userDetails.getUser().getUserId());
		
		if (menuCategoryService.existsByStoreAndName(store, name)) {
			throw new IllegalArgumentException("같은 이름의 카테고리가 이미 존재합니다.");
		}
		
		MenuCategory category = menuCategoryService.save(name, store);
		return new MenuCategoryDto(category);
	}

	@GetMapping("/list")
	public List<MenuCategoryDto> list(@RequestParam UUID storeId) {
		Store store = storeService.findById(storeId);
		return menuCategoryService.findByStore(store);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") UUID id) {
		System.out.println("🗑 카테고리 삭제 요청: " + id);
		menuCategoryService.deleteById(id);
	}

	@PutMapping("/{id}")
	public MenuCategory update(@PathVariable UUID id, @RequestParam String name) {
		return menuCategoryService.update(id, name);
	}
}