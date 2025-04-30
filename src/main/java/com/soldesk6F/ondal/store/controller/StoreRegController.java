package com.soldesk6F.ondal.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class StoreRegController {

	@GetMapping("/storeReg")
	public String storeReg() {
		return "content/store/storeReg";
	}

	@GetMapping("/storeReg/submit")
	public String storeSubmit() {
		return "content/store/submit";
	}
	
	@GetMapping("/content/store/storeRegSuccess")
	public String showStoreRegSuccessPage() {
	    return "content/store/storeRegSuccess";
	}
	
	@GetMapping("/storelist")
	public String StoreList() {
		return "content/store/storelist";
	}
	
	
	
}




