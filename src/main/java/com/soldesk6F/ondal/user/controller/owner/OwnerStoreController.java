package com.soldesk6F.ondal.user.controller.owner;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.store.entity.Store;
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
	        System.out.println("   - 가게명: " + s.getStoreName());
	    }

		model.addAttribute("myStores", myStores);

		return "content/owner/ownerStoreList";
	}
}
