package com.soldesk6F.ondal;

import org.springframework.security.core.Authentication;
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

	@GetMapping("/")
	public String home() {
		return "content/index";
	}
	
	@GetMapping("/login")
	public String login() {
		return "content/login";
		
	}
	
	@GetMapping("/whoami")
	public String whoAmI() {
//	    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//	    User user = userDetails.getUser();
//	    System.out.println("User: " + user.getEmail());
//	    System.out.println("Principal toString(): " + userDetails.toString());

	    return "content/test";  // 혹은 원하는 뷰
	}	
	
	
}
