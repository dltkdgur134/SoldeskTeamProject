package com.soldesk6F.ondal.user.controller.rider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.Rider.DeliveryRange;
import com.soldesk6F.ondal.user.repository.RiderRepository;
import com.soldesk6F.ondal.user.service.RiderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UpdateRiderController {
	private final RiderRepository riderRepository;
	private final RiderService riderService;

	@Autowired
	private PasswordEncoder passwordEncoder; // 필드 주입
	
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
	        @RequestParam("currentSecondaryPassword") String currentSecondaryPassword,
	        @RequestParam("newSecondaryPassword") String newSecondaryPassword,
	        @RequestParam("confirmNewSecondaryPassword") String confirmNewSecondaryPassword,
	        @RequestParam("vehicleNumber") String vehicleNumber,
	        @RequestParam("riderPhone") String riderPhone,
	        @RequestParam("riderHubAddress") String riderHubAddress, 
	        @RequestParam("hubAddressLatitude") double hubAddressLatitude,
	        @RequestParam("hubAddressLongitude") double hubAddressLongitude,
	        @RequestParam("deliveryRange") String deliveryRange,
	        @AuthenticationPrincipal CustomUserDetails userDetails,
	        RedirectAttributes redirectAttributes) {
	    String userId = userDetails.getUser().getUserId();
	    // 2차 비밀번호 확인 로직
	    if (!newSecondaryPassword.equals(confirmNewSecondaryPassword)) {
	    	redirectAttributes.addFlashAttribute("SecondaryPWError", "새로운 2차 비밀번호와 확인이 일치하지 않습니다.");
	        return "redirect:/rider/riderInfopage";  // 에러 메시지를 포함한 뷰로 리턴
	    }

	    // 라이더 비밀번호 확인
	    Rider rider = riderRepository.findByUser_UserId(userId)
	            .orElseThrow(() -> new IllegalArgumentException("라이더 정보가 존재하지 않습니다."));
	    
	    boolean passwordMatches = passwordEncoder.matches(currentSecondaryPassword, rider.getSecondaryPassword());
	    if (!passwordMatches) {
	    	redirectAttributes.addFlashAttribute("SecondaryPWError", "현재 2차 비밀번호가 일치하지 않습니다. 다시 확인해주세요.");
	        return "redirect:/rider/riderInfopage";  // 에러 메시지를 포함한 뷰로 리턴
	    }

	    // 2차 비밀번호 업데이트
	    riderService.updateRiderSecondaryPassword(rider, newSecondaryPassword);
	    redirectAttributes.addFlashAttribute("SecondaryPWSuccess", "2차 비밀번호가 정상적으로 변경되었습니다.");
	    // 라이더 정보 수정
	    DeliveryRange rangeEnum = DeliveryRange.valueOf(deliveryRange);
	    riderService.updateRiderInfo(userId, null, vehicleNumber, riderPhone, riderHubAddress, 
	                                 hubAddressLatitude, hubAddressLongitude, rangeEnum);

	    return "redirect:/rider/riderInfopage"; 
	}

}
