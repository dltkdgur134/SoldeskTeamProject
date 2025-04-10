package com.soldesk6F.ondal.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PassCheckController {

	@GetMapping("/oauth/passCheck")
    public String showLinkPage(@RequestParam("provider") String email, Model model) {
        model.addAttribute("email", email); 
        return "content/passCheck";
    }
	
	
	
	
	
	
}
