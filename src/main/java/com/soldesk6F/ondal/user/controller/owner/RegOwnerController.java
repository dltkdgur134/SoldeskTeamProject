package com.soldesk6F.ondal.user.controller.owner;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.user.service.OwnerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class RegOwnerController {
	private final OwnerService ownerService;
	private final UserRepository userRepository;
	
	
	@GetMapping("/register")
	public String showOwnerRegistratinForm(@AuthenticationPrincipal CustomUserDetails userDetails,
			RedirectAttributes redirectAttributes) {
		String userId = userDetails.getUser().getUserId();
		
		if (ownerService.isAlreadyOwner(userId)) {
        	redirectAttributes.addFlashAttribute("ownerExists", true);
        	 return "redirect:/"; 
        }
		 return "content/owner/ownerRegister";
	}

}
