package com.soldesk6F.ondal;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class OwnerController {

	@GetMapping("/store")
	public String store() {
		return "content/store/storeMenu";
	}
	
	@GetMapping("/storesetting")
	public String storeSetting() {
		return "content/store/storeSetting";
	}

    @GetMapping("/setting/operation")
    public String operationSetting() {
        return "content/setting/operation";
    }
    
    @GetMapping("/setting/printer")
    public String printerSetting() {
        return "content/setting/printer";
    }
    
    @GetMapping("/setting/alarm")
    public String alarmSetting() {
        return "content/setting/alarm";
    }
    
    @GetMapping("/setting/delivery")
    public String deliverySetting() {
        return "content/setting/deliveryAgency";
    }

}



