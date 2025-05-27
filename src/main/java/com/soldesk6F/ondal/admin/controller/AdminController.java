package com.soldesk6F.ondal.admin.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
        return "content/admin/ownerRiderapproval";
    }
    
    @GetMapping(value = "/setting/customerService")
    public String deliverySetting() {
        return "content/admin/customerService";
    }
	
}
