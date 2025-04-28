package com.soldesk6F.ondal.search;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchStoreInRadiusController {
	
	private 
	
	
	@GetMapping("/searchStoreInRadius")
	public String searchStoreInRadius(@RequestParam("x")String x , @RequestParam("y") String y , @RequestParam("keyword") String keyword ){
		
		
		
		
		return "1";
	}
	
	
	
	
	
	
	
	

}
