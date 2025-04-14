package com.soldesk6F.ondal.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PassCheckController {

	private final SinkService sinkService;
	
	
	@GetMapping("/oauth/passCheck")
    public String showLinkPage(@RequestParam("userId") String userId, Model model) {
        model.addAttribute("userId", userId); 
        return "content/passCheck";
    }
	
	@PostMapping("/oauth/trySink")
	public String trySink(@RequestParam("userId") String userId, @RequestParam("password") String password
			,@RequestParam("overwriteProfile") boolean overrideProfile, Model model) {
		
		if(sinkService.trySink(userId, password,overrideProfile)) {
			return "redirect:/";	
			
		}
			return "redirect:/oauth/passCheck?userId=" + userId;
		
		
		
	}	
	
	
	
	
	
	
}
