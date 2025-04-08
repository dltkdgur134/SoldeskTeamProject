package com.soldesk6F.ondal;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

//	@GetMapping("/")
//	public String home() {
//		return "content/index";
//	}
	
	@GetMapping("/login")
	public String login() {
		return "content/login";
		
	}
	
	@GetMapping("/")
	public String main(Principal principal) {

		if (principal != null) {
			System.out.println("타입정보 : " + principal.getClass());
			System.out.println("ID정보 : " + principal.getName());
		}
		return "content/index";
	}
   
	@GetMapping("/loggedin")
	public String testLog(Principal principal, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		System.out.println(username);
		return "content/infopage";
	}
	
	
}
