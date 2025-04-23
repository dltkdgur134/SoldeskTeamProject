package com.soldesk6F.ondal.menu.service;

import com.soldesk6F.ondal.menu.dto.MenuCategoryDto;
import com.soldesk6F.ondal.menu.entity.MenuCategory;
import com.soldesk6F.ondal.menu.repository.MenuCategoryRepository;
import com.soldesk6F.ondal.store.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuCategoryService {

	private final MenuCategoryRepository menuCategoryRepository;

	public MenuCategory save(String categoryName, Store store) {
		MenuCategory category = MenuCategory.builder()
				.categoryName(categoryName)
				.store(store)
				.build();
		
		System.out.println(" 저장된 카테고리 이름: " + category.getCategoryName()); // 로그 체크용
		
		return menuCategoryRepository.save(category);
	}

	public List<MenuCategoryDto> findByStore(Store store) {
		return menuCategoryRepository.findByStore(store).stream()
			.map(MenuCategoryDto::new)
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteById(UUID id) {
		menuCategoryRepository.deleteById(id);
	}
	
	public boolean existsByStoreAndName(Store store, String name) {
		return menuCategoryRepository.existsByStoreAndCategoryName(store, name);
	}
	
	public void deleteAllByStore(Store store) {
		menuCategoryRepository.deleteAllByStore(store);
	}
	
	public MenuCategory findById(UUID id) {
		return menuCategoryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
	}
	
	

	public MenuCategory update(UUID id, String newName) {
		MenuCategory category = menuCategoryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
		category.setCategoryName(newName);
		return menuCategoryRepository.save(category);
	}
}