package com.soldesk6F.ondal.user.controller.owner;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.menu.dto.MenuDto;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.entity.StoreDto;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.store.service.StoreService;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerStoreController {
	private final UserService userService;
	private final StoreService storeService;
	private final StoreRepository storeRepository;
	
	@GetMapping("/ownerStoreList")
	public String getOwnerStores(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
//		if (principal == null) {
//			redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
//			return "redirect:/login";
//		}

		UUID userUuid = userDetails.getUser().getUserUuid();
		System.out.println("로그인한 사용자 UUID: " + userUuid);

		Optional<Owner> ownerOpt = userService.findOwnerByUserUuid(userUuid.toString());

		if (ownerOpt.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "점주만 접근할 수 있습니다.");
			return "redirect:/";
		}

		Owner owner = ownerOpt.get();

		List<Store> myStores = storeService.findStoresByOwner(owner);
		System.out.println("📦 해당 점주의 가게 수: " + myStores.size());

		model.addAttribute("myStores", myStores);

		return "content/owner/ownerStoreList";
	}
	
	private String formatPhoneNumber(String phone) {
		if (phone == null) return "";
		phone = phone.replaceAll("[^0-9]", "");

		if (phone.length() == 11) {
			return phone.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
		} else if (phone.length() == 10) {
			return phone.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
		} else if (phone.length() == 9) {
			return phone.replaceFirst("(\\d{2})(\\d{3})(\\d{4})", "$1-$2-$3");
		} else {
			return phone;
		}
	}
	
	@GetMapping("/storeManagement/{storeId}")
	public String manageStore(@PathVariable("storeId") UUID storeId, 
							@AuthenticationPrincipal CustomUserDetails userDetails,
							Model model, RedirectAttributes redirectAttributes) {
	    Store store = storeRepository.findById(storeId)
	            .orElseThrow(() -> new IllegalArgumentException("해당 점포를 찾을 수 없습니다."));
	    
	    String formattedPhone = formatPhoneNumber(store.getStorePhone());
	    System.out.println("형식 포맷된 전화번호: " + formattedPhone);
	    model.addAttribute("store", store);
	    model.addAttribute("formattedPhone", formattedPhone);
	    return "content/store/storeManagement2";
	}
	
//	@GetMapping("/storeManagement/{storeId}/menu-manage")
//	public String menuManagePage(@PathVariable UUID storeId, Model model) {
//	    StoreDto store = storeService.getStoreById(storeId);
//	    List<MenuDto> menuList = MenuService.getMenusByStore(storeId);
//	    
//	    model.addAttribute("store", store);
//	    model.addAttribute("menuList", menuList);
//	    
//	    return "content/store/storeMenuManage";
//	}
//	
	
}
