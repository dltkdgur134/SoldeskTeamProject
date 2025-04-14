package com.soldesk6F.ondal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.soldesk6F.ondal.login.CustomUserDetails;

@Controller
public class PageController {
	
	@GetMapping (value = "/userlogin")
	public String goUserLogin(Model model) {
		
		return "login";
	}
	
	@GetMapping (value = "/infopage")
	public String goInfoPage(Model model) {
		
		return "content/infopage";
	}
	
	@GetMapping (value = "/mypage")
	public String goMyPage(Model model) {
		return "content/mypage";
	}
	
}
