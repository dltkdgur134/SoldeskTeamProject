package com.soldesk6F.ondal.user.controller.rider;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.repository.RiderRepository;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderHomeController {

    private final RiderRepository riderRepository;

    @GetMapping("/home")
    public String riderHomeGet(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String userId = userDetails.getUser().getUserId();

        Optional<Rider> optionalRider = riderRepository.findByUser_UserId(userId);

        if (optionalRider.isPresent()) {
            Rider rider = optionalRider.get();

            // 디버깅 로그
            System.out.println("✅ Rider 불러오기 성공!");
            System.out.println("📍 위도: " + rider.getHubAddressLatitude());
            System.out.println("📍 경도: " + rider.getHubAddressLongitude());
            System.out.println("🆔 riderId: " + rider.getRiderId());
            System.out.println("📅 등록일: " + rider.getRegistrationDate());

            model.addAttribute("rider", rider);
        } else {
            System.out.println("❌ Rider 정보 없음. userId = " + userId);
        }

        return "content/rider/riderHome";
    }
    
    @GetMapping("/myPage")
    public String showMyPage() {
    	return "redirect:/myPage"; // templates/user/infopage.html 이라는 뷰 파일을 반환
    }
    
    @GetMapping("/riderInfopage")
    public String showRiderMyPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
    	String userId = userDetails.getUser().getUserId();
    	
    	Optional<Rider> optionalRider = riderRepository.findByUser_UserId(userId);

        if (optionalRider.isPresent()) {
            Rider rider = optionalRider.get();
            model.addAttribute("rider", rider);
        } else {
            // 예외 처리나 에러 페이지로 이동 가능
        }
    	
    	
    	
    	return "content/rider/riderInfopage"; 
    }
    
    
    
    
}


