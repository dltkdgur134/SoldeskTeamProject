package com.soldesk6F.ondal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
	
	@GetMapping (value = "/userlogin")
	public String goUserLogin(Model model) {
		
		return "login";
	}
	
	@GetMapping (value = "/infopage")
	public String goMyPage(Model model) {
		
		return "content/infopage";
	}
	
}
