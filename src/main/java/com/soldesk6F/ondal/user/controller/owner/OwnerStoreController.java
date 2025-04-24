package com.soldesk6F.ondal.user.controller.owner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.menu.dto.MenuCategoryDto;
import com.soldesk6F.ondal.menu.dto.MenuDto;
import com.soldesk6F.ondal.menu.dto.MenuOrderDto;
import com.soldesk6F.ondal.menu.dto.MenuRegisterDto;
import com.soldesk6F.ondal.menu.entity.MenuCategory;
import com.soldesk6F.ondal.menu.service.MenuCategoryService;
import com.soldesk6F.ondal.menu.service.MenuService;
import com.soldesk6F.ondal.store.entity.Store;
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
	private final MenuService menuService;
	private final MenuCategoryService menuCategoryService;
	
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
		
		for (Store s : myStores) {
	        System.out.println("##################   - 가게명: " + s.getStoreName());
	        System.out.println("✅ storeId: " + s.getStoreId());
	    }

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
	
	@GetMapping("/storeManagement/{storeId}/menu-manage")
	public String menuManagePage(
			@PathVariable("storeId") UUID storeId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
	
		String loginUserId = userDetails.getUser().getUserId();
		Store store = storeService.getStoreForOwner(storeId, loginUserId);
		List<MenuDto> menuList = menuService.getMenusByStore(store);
		List<MenuCategoryDto> categoryList = menuCategoryService.findByStore(store);
		
		ObjectMapper mapper = new ObjectMapper();
		String categoryListJson = "";
		try {
			categoryListJson = mapper.writeValueAsString(categoryList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.addAttribute("store", store);
		model.addAttribute("menuList", menuList);
		model.addAttribute("categoryListJson", categoryListJson);
		
		if (!model.containsAttribute("result")) {
	        model.addAttribute("result", -1);
	    }
	    if (!model.containsAttribute("resultMsg")) {
	        model.addAttribute("resultMsg", "");
	    }
		
		return "content/store/storeMenuManage";
	}
	
	@PostMapping("/storeManagement/{storeId}/menu-register")
	public String registerMenu(@PathVariable("storeId") UUID storeId,
	                           @ModelAttribute MenuRegisterDto menuDto,
	                           @AuthenticationPrincipal CustomUserDetails userDetails,
	                           RedirectAttributes redirectAttributes) {
		
	    try {
	        menuService.registerMenu(storeId, menuDto, userDetails.getUser().getUserId());
	        redirectAttributes.addFlashAttribute("result", 0);
	        redirectAttributes.addFlashAttribute("resultMsg", "메뉴가 등록되었습니다.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("result", 1);
	        redirectAttributes.addFlashAttribute("resultMsg", "메뉴 등록에 실패했습니다.");
	    }
	    System.out.println(menuDto.getMenuCategoryId());
	    return "redirect:/owner/storeManagement/" + storeId + "/menu-manage";
	}
	
	@PostMapping("/storeManagement/{storeId}/menu-edit")
	public String editMenu(@PathVariable("storeId") UUID storeId,
	                       @ModelAttribute MenuRegisterDto menuDto,
	                       @AuthenticationPrincipal CustomUserDetails userDetails,
	                       RedirectAttributes redirectAttributes) {

		try {
			menuService.editMenu(storeId, menuDto, userDetails.getUser().getUserId());
			redirectAttributes.addFlashAttribute("result", 0);
			redirectAttributes.addFlashAttribute("resultMsg", "수정되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "수정에 실패했습니다.");
		}
		return "redirect:/owner/storeManagement/" + storeId + "/menu-manage";
	}
	
	@PostMapping("/storeManagement/{storeId}/menu-delete")
	public String deleteMenu(@PathVariable("storeId") UUID storeId, @RequestParam("menuId") UUID menuId, RedirectAttributes redirectAttributes) {
		try {
			menuService.deleteMenu(menuId);
			redirectAttributes.addFlashAttribute("message", "메뉴가 삭제되었습니다.");
		} catch (Exception e) {
		redirectAttributes.addFlashAttribute("error", "메뉴 삭제 중 오류 발생: " + e.getMessage());
		}
		return "redirect:/owner/storeManagement/" + storeId + "/menu-manage";
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
