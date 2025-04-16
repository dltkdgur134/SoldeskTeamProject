package com.soldesk6F.ondal;

import org.springframework.security.core.Authentication;
import java.security.Principal;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.soldesk6F.ondal.user.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.User;

@Controller
public class HomeController {

    private final OndalApplication ondalApplication;

    HomeController(OndalApplication ondalApplication) {
        this.ondalApplication = ondalApplication;
    }

//	@GetMapping("/")
//	public String home() {
//		return "content/index";
//	}
	
	@GetMapping("/")
	public String home() {
		return "owner/content/ownerDashboard";
	}
	
	@GetMapping("/login")
	public String login() {
		return "content/login";
		
	}
	

}
