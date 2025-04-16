package com.soldesk6F.ondal;

import com.soldesk6F.ondal.store.entity.StoreDto;
//import com.soldesk6F.ondal.store.service.StoreService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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




