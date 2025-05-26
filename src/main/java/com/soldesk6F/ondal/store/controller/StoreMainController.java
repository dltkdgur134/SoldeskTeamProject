package com.soldesk6F.ondal.store.controller;

import java.security.Principal;
import java.util.Map;
import java.util.stream.Collectors;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.service.StoreService;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.favorites.repository.FavoritesRepository;
import com.soldesk6F.ondal.useract.review.repository.ReviewRepository;
import com.soldesk6F.ondal.menu.service.MenuService;
import com.soldesk6F.ondal.menu.dto.MenuDto;
import com.soldesk6F.ondal.menu.entity.MenuCategory;
import com.soldesk6F.ondal.menu.repository.MenuCategoryRepository;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;
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
	private final UserRepository userRepository;
	private final FavoritesRepository favoritesRepository;
	private final ReviewRepository reviewRepository;
	private final OrderRepository orderRepository;
	private final MenuCategoryRepository menuCategoryRepository;

	@GetMapping("/store/view/{storeId}")
	public String viewStore(@PathVariable("storeId") UUID storeId, Model model, Principal principal) {
		Store store = storeService.findByIdWithImgs(storeId);
		if (store == null) {throw new IllegalArgumentException("가게를 찾을 수 없습니다.");}
		List<MenuDto> menus = menuService.getMenusByStore(store);
		//Map<String, List<MenuDto>> groupedMenus = menus.stream()
		//	.collect(Collectors.groupingBy(MenuDto::getMenuCategory));
		Map<String, List<MenuDto>> groupedMenus = menus.stream()
				.collect(Collectors.groupingBy(menu -> {
					String category = menu.getMenuCategory();
			        return category != null ? category : "없음";
				}));
		model.addAttribute("groupedMenus", groupedMenus);

		model.addAttribute("store", store);
		model.addAttribute("storeImgs", store.getStoreImgs());
		
		boolean isFavorite = false;
		if (principal != null) {
			String userId = principal.getName();
			User user = userRepository.findByUserId(userId).orElse(null);
			if (user != null) {
				isFavorite = favoritesRepository.existsByUserAndStore(user, store);
			}
		}
		model.addAttribute("isFavorite", isFavorite);
		
		long reviewCount = reviewRepository.countByStore(store);
		double avgRating = reviewRepository.findAverageRatingByStore(store);
		model.addAttribute("reviewCount", reviewCount);
		model.addAttribute("avgRating", String.format("%.1f", avgRating));
		
		List<MenuCategory> categoryList = menuCategoryRepository.findByStoreOrderByCategoryOrder(store);
		model.addAttribute("menuCategories", categoryList);
		
		long orderCount = orderRepository.countByStore(store);
		model.addAttribute("orderCount", orderCount);

		long favoriteCount = favoritesRepository.countByStore(store);
		model.addAttribute("favoriteCount", favoriteCount);

		String ownerName = store.getOwner().getUser().getUserName();
		model.addAttribute("ownerName", ownerName);
		
		String formattedPhone = formatPhoneNumber(store.getStorePhone());
		model.addAttribute("formattedPhone", formattedPhone);

		return "content/store/storeMain";
	}
	
	public static String formatPhoneNumber(String raw) {
		if (raw == null || raw.length() < 9) return raw;

		if (raw.startsWith("02")) {
			if (raw.length() == 9) {
				return raw.replaceFirst("(02)(\\d{3})(\\d{4})", "$1-$2-$3");
			} else {
				return raw.replaceFirst("(02)(\\d{4})(\\d{4})", "$1-$2-$3");
			}
		} else {
			return raw.replaceFirst("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
		}
	}
}