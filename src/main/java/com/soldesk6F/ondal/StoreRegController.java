package com.soldesk6F.ondal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class StoreRegController {

	@GetMapping("/storeReg")
	public String storeReg() {
		return "content/storeReg";
	}

	@GetMapping("/storeReg/submit")
	public String storeSubmit() {
		return "content/storereg/submit";
	}
	
}




