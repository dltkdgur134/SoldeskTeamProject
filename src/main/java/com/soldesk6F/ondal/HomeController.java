package com.soldesk6F.ondal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home() {
		return "index";
		
	}
	
	@GetMapping("/test")
	public String test(Model model) {
		model.addAttribute("menu", "햄버거");
		return "example";
	}
	
	@GetMapping("/test2")
	public String test2() {
		return "test2";
	}
	
}
