package com.soldesk6F.ondal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
	
	@GetMapping("/access-denied")
	public String accessDenied(RedirectAttributes redirectAttributes, Model model) {
		redirectAttributes.addFlashAttribute("result", 1);
		redirectAttributes.addFlashAttribute("resultMsg", "접근 권한이 없습니다.");
		return "redirect:/";
	}
	
}
