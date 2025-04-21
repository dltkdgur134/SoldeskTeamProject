package com.soldesk6F.ondal.user.controller.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.repository.OwnerRepository;
import com.soldesk6F.ondal.user.service.OwnerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UpdateOwnerController {
	private final OwnerRepository ownerRepository;
	private final OwnerService ownerService;
	
	@Autowired
	private PasswordEncoder passwordEncoder; // 필드 주입

	@GetMapping("owner/ownerMypage")
    public String showEditPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		String userId = userDetails.getUser().getUserId();
		Owner owner = ownerService.getOwnerByUserId(userId);
		model.addAttribute("owner", owner);
		
		return "content/owner/ownerMypage"; //닉네임 수정 폼 페이지
    }
	
	@GetMapping("owner/updateOwnerInfo")
	public String showUpdateRiderInfoForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		String userId = userDetails.getUser().getUserId();
		Owner owner = ownerService.getOwnerByUserId(userId);
		model.addAttribute("owner",owner);
		return "content/owner/ownerUpdateInfo"; // 수정 폼 페이지
	}
	
	
	
}
