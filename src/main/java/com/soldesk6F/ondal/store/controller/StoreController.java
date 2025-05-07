package com.soldesk6F.ondal.store.controller;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.entity.StoreDto;
import com.soldesk6F.ondal.store.entity.StoreRegisterDto;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.store.service.StoreService;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.OwnerRepository;

import lombok.RequiredArgsConstructor;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
	private final OwnerRepository ownerRepository;
	private final StoreRepository storeRepository;

    @GetMapping("/api/stores/{category}")
    @ResponseBody
    public List<StoreDto> getStoresByCategory(@PathVariable("category") String category) {
        return storeService.getStoresByCategory(category);
    }
    
    @PostMapping("/store/register")
    public String registerStore(@ModelAttribute StoreRegisterDto dto,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model) {
        try {
            storeService.registerStore(dto, userDetails.getUser());
            return "redirect:/content/store/storeRegSuccess";
        } catch (Exception e) {
            model.addAttribute("error", "점포 등록 중 오류가 발생했습니다.");
            return "content/store/submit";
        }
    }
    
	@GetMapping("/store/list")
	public String showStoreList(@RequestParam(name = "category", required = false) String category,
    							Model model) {
    	model.addAttribute("selectedCategory", category);
        return "content/store/storeList";
	}
	
	@GetMapping("/owner/storeList")
	public String viewMyStores(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
	    User user = userDetails.getUser();

	    if (user.getUserRole() != User.UserRole.OWNER && user.getUserRole() != User.UserRole.ALL) {
	        return "redirect:/";
	    }

	    Owner owner = ownerRepository.findByUser_UserId(user.getUserId())
	            .orElseThrow(() -> new IllegalStateException("점주 정보가 없습니다."));

	    List<Store> storeList = storeRepository.findByOwner(owner);

	    model.addAttribute("myStores", storeList);
	    return "content/owner/ownerStoreList";
	}

}




