package com.soldesk6F.ondal.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@GetMapping("/home")
	public String home() {
		return "content/admin/adminpage";
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
