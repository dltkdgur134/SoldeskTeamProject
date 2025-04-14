package com.soldesk6F.ondal.user.controller.rider;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soldesk6F.ondal.user.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.Rider.DeliveryRange;
import com.soldesk6F.ondal.user.service.RiderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UpdateRiderController {
	private final RiderService riderService;

	@GetMapping("rider/riderMypage")
    public String showEditPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		String userId = userDetails.getUser().getUserId();
		Rider rider = riderService.getRiderByUserId(userId); // rider 정보 불러오기
		model.addAttribute("rider", rider);
		return "content/rider/riderMypage"; 
    }
	
	
	@GetMapping("rider/updateRiderInfo")
	public String showUpdateRiderInfoForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		String userId = userDetails.getUser().getUserId();
		Rider rider = riderService.getRiderByUserId(userId); // rider 정보 불러오기
		model.addAttribute("rider", rider);
		return "content/rider/riderUpdateInfo"; // 수정 폼 페이지
	}

	@PostMapping("/updateRiderNickname")
	public String updateRiderNickname(@RequestParam("riderNickname") String riderNickname,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		String userId = userDetails.getUser().getUserId();
		riderService.updateRiderInfo(userId, riderNickname, null, null, null, 0, 0, null);
		return "redirect:/rider/riderInfopage"; // 혹은 리다이렉션할 페이지 경로
	}

	@PostMapping("/updateRiderInfo")
	public String updateRiderInfo(
			@RequestParam("vehicleNumber") String vehicleNumber,
			@RequestParam("riderPhone") String riderPhone,
			@RequestParam("riderHubAddress") String riderHubAddress, 
			@RequestParam("hubAddressLatitude") double hubAddressLatitude,
			@RequestParam("hubAddressLongitude") double hubAddressLongitude,
			@RequestParam("deliveryRange") String deliveryRange,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		String userId = userDetails.getUser().getUserId();
		DeliveryRange rangeEnum = DeliveryRange.valueOf(deliveryRange);
		riderService.updateRiderInfo(userId, null, vehicleNumber,riderPhone, riderHubAddress,  hubAddressLatitude,
				hubAddressLongitude, rangeEnum);
		return "redirect:/rider/riderInfopage";
		
	}

}
