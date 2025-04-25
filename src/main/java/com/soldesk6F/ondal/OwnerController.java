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
	
	@GetMapping("/store-management")
	public String storeManagement(Model model) {
	    Store mockStore = Store.builder()
	        .storeName("온달닭갈비")
	        .storeAddress("서울시 강남구 역삼동 123")
	        .storePhone("02-1234-5678")
	        .storeStatus(StoreStatus.OPEN) // enum 등 설정 필수
	        .build();

	    model.addAttribute("store", mockStore);
	    model.addAttribute("formattedPhone", "02-1234-5678");
	    return "content/store/storeManagement";
	}

	@GetMapping("/store-management/{storeId}")
	public String store(@PathVariable("storeId") UUID storeId, Model model, HttpSession session) {
	    session.setAttribute("storeId", storeId); // ✅ 세션에 저장
	    Store store = storeService.findStoreByStoreId(storeId);
	    model.addAttribute("store", store);

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



