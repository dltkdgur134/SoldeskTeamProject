package com.soldesk6F.ondal.useract.regAddress.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.useract.regAddress.DTO.RegAddressDTO;
import com.soldesk6F.ondal.useract.regAddress.service.RegAddressService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegAddressController {

	private final RegAddressService regAddressService;

	@PostMapping("/content/regAddress")
	public String registerHomeAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("address") String address, @RequestParam("detailAddress") String detailAddress,
			@RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude,
			RedirectAttributes redirectAttributes) {
		regAddressService.regAddress(userDetails, redirectAttributes, address, detailAddress, latitude, longitude);
		return "redirect:/myAddress";
	}


	@GetMapping(value = "/myAddress")
	public String goMyAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
			RedirectAttributes redirectAttributes, Model model) {
		regAddressService.getAllRegAddress(userDetails, redirectAttributes, model);
		return "content/myAddress";
	}

	@GetMapping(value = "/regAddress")
	public String goRegAddress() {
		return "content/regAddress";
	}
	
	@GetMapping("/updateAddress/{regAddressId}")
	public String goUpdateAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("regAddressId") UUID regAddressId,
			RedirectAttributes redirectAttributes, Model model) {
		regAddressService.getRegAddress(userDetails, regAddressId, redirectAttributes, model);
		return "content/updateAddress";
	}
	
	@PostMapping("/content/setDefaultAddress")
	public String changeUserSelectedAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("regAddressId") UUID regAddressId, 
			RedirectAttributes redirectAttributes) {
		regAddressService.selectDefaultAddress(userDetails, regAddressId, redirectAttributes);
		return "redirect:/myAddress";
	}
	
	@PutMapping("/content/updateAddress")
	public String updateAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
			RegAddressDTO regAddressDTO,
			RedirectAttributes redirectAttributes) {
		regAddressService.updateAddress(userDetails, regAddressDTO, redirectAttributes);
		return "redirect:/myAddress";
	}
	
	@DeleteMapping("/content/deleteAddress/{regAddressId}")
	public ResponseEntity<Map<String, Object>> deleteAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("regAddressId") UUID regAddressId) {
		boolean result = regAddressService.deleteAddress(userDetails, regAddressId);
		Map<String, Object> response = new HashMap<>();
		if (result) {
			response.put("result", 0);
			response.put("resultMsg", "주소가 삭제되었습니다.");
			return ResponseEntity.ok(response);
		} else {
			response.put("result", 1);
			response.put("resultMsg", "주소가 삭제에 실패했습니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

}
