package com.soldesk6F.ondal.useract.regAddress.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.useract.regAddress.service.RegAddressService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegAddressController {
	
	private final RegAddressService regAddressService;
	
	@PostMapping("/content/regHomeAddress")
	public String registerHomeAddress(
			@AuthenticationPrincipal CustomUserDetails cud,
			@RequestParam("address") String address,
			@RequestParam("detailAddress") String detailAddress,
			@RequestParam("longitude") String longitude,
			@RequestParam("latitude") String latitude,
			RedirectAttributes rAttr) {
		
//		if (!userService.regHomeAddress(cud, rAttr, address, detailAddress, latitude, longitude)) {
//			return "redirect:/myPage";
//		}
		regAddressService.regAddress(cud, rAttr, address, detailAddress, latitude, longitude);
		return "redirect:/myPage";
	}
	
	@GetMapping (value = "/myAddress")
	public String goMyAddress(@AuthenticationPrincipal CustomUserDetails cud,
			RedirectAttributes rAttr,
			Model model) {
		regAddressService.getRegAddress(cud, rAttr, model);
		return "content/myAddress";
	}
	
	@GetMapping (value = "/regAddress")
	public String goRegAddress() {
		return "content/regAddress";
	}
	
	
}
