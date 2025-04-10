package com.soldesk6F.ondal;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.soldesk6F.ondal.user.CustomUserDetails;

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
