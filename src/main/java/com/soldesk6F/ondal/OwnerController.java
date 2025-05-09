package com.soldesk6F.ondal;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.entity.Store.StoreStatus;
import com.soldesk6F.ondal.store.service.StoreService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
@RequestMapping("/owner")
public class OwnerController {
	
	private final StoreService storeService;

	@GetMapping("/store-management/{storeId}")
	public String store(@PathVariable("storeId") UUID storeId, Model model) {
	    // 2) 모델에 매장 정보와 storeId 담기
	    Store store = storeService.findStoreByStoreId(storeId);
	    model.addAttribute("store", store);
	    model.addAttribute("storeId", store.getStoreId().toString()); // Thymeleaf에서 data-storeid로 쓰기 위함

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
    
    @GetMapping(value = "/setting/printer")
    public String printerSetting() {
        return "content/setting/printer";
    }
    
    @GetMapping(value = "/setting/alarm")
    public String alarmSetting() {
        return "content/setting/alarm";
    }
    
    @GetMapping(value = "/setting/delivery")
    public String deliverySetting() {
        return "content/setting/deliveryAgency";
    }

}



