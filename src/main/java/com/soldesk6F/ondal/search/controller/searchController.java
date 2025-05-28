package com.soldesk6F.ondal.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/search")
public class searchController {
	
	
	
	@GetMapping("/storeInRadius")
	public String searchStoreInRadius() {
		
		
		
		return "content/login";
	}
	
	
	
	
	
	
	
	
	
	

}
