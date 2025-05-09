package com.soldesk6F.ondal.store.controller;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.service.StoreService;
import com.soldesk6F.ondal.menu.service.MenuService;
import com.soldesk6F.ondal.menu.dto.MenuDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class StoreMainController {

	private final StoreService storeService;
	private final MenuService menuService;

	@GetMapping("/store/view/{storeId}")
	public String viewStore(@PathVariable("storeId") UUID storeId, Model model) {
		Store store = storeService.findByIdWithImgs(storeId);
		if (store == null) {
			throw new IllegalArgumentException("가게를 찾을 수 없습니다.");
		}
		List<MenuDto> menus = menuService.getMenusByStore(store);

		model.addAttribute("store", store);
		model.addAttribute("menus", menus);
		model.addAttribute("storeImgs", store.getStoreImgs());

		return "content/store/storeMain";
	}
}