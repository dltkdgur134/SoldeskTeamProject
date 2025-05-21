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
	
    @GetMapping(value = "/setting/usersetting")
    public String operationSetting() {
        return "content/admin/usersetting";
    }
    
    @GetMapping(value = "/setting/storeapproval")
    public String printerSetting() {
        return "content/admin/storeApproval";
    }
    
    @GetMapping(value = "/setting/ownerRiderapproval")
    public String alarmSetting() {
        return "content/admin/ownerRiderApproval";
    }
    
    @GetMapping(value = "/setting/customerService")
    public String deliverySetting() {
        return "content/admin/customerService";
    }
	
}
