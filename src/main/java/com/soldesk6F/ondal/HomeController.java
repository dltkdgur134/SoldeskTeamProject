package com.soldesk6F.ondal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home() {
		return "content/index";
	}
	
	@GetMapping("/login")
	public String login() {
		return "content/login";
		
	}
	
}
