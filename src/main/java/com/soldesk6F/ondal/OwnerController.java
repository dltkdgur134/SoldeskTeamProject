package com.soldesk6F.ondal;


import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.service.StoreService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
@RequestMapping("/owner")
public class OwnerController {
	
	private final StoreService storeService;

	@GetMapping("/store-management/{storeId}")
	public String store(@PathVariable("storeId") UUID storeId, 
			@AuthenticationPrincipal CustomUserDetails userDetails,
			Model model,
			RedirectAttributes redirectAttributes) {
	    // 2) 모델에 매장 정보와 storeId 담기
	    Store store = storeService.findStoreByStoreId(storeId);
	    // 현재 접속한 유저가 해당 store 의 owner 의 유저 객체와 일치하는지 확인
	    String ownerUserUuid = store.getOwner().getUser().getUserUuidAsString();
	    String userUuid = userDetails.getUser().getUserUuidAsString();
	    
	    if (!ownerUserUuid.equals(userUuid)) {
	    	return "redirect:/";
	    }
	    model.addAttribute("store", store);
	    model.addAttribute("storeId", storeId.toString()); // Thymeleaf에서 data-storeid로 쓰기 위함
	    
	    return "content/store/storeManagement";
	}
	
	@GetMapping(value = "/storesetting")
	public String storeSetting() {
		return "content/store/storeSetting";
	}

    @GetMapping(value = "/setting/operation")
    public String operationSetting() {
        return "content/setting/operation";
    }
    
    @GetMapping(value = "/setting/ordersetting")
    public String printerSetting() {
        return "content/setting/ordersetting";
    }
    
    @GetMapping(value = "/setting/deliverysetting")
    public String alarmSetting() {
        return "content/setting/deliverysetting";
    }
    
    @GetMapping(value = "/setting/deliveryAgency")
    public String deliverySetting() {
        return "content/setting/deliveryAgency";
    }

}



