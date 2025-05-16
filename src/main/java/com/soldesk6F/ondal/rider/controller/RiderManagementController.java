package com.soldesk6F.ondal.rider.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.rider.dto.RiderManagementDto;
import com.soldesk6F.ondal.rider.service.RiderManagementService;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.repository.RiderRepository;
import com.soldesk6F.ondal.user.service.RiderService;

import lombok.RequiredArgsConstructor;
@Controller
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderManagementController {
	private final RiderManagementService riderManagementService;
	private final RiderRepository riderRepository;

    @GetMapping("/management")
    public String showManagementPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String userId = userDetails.getUser().getUserId();
        Rider rider = riderRepository.findByUser_UserId(userId).orElseThrow();
        
        // 서비스 호출하여 Rider의 관리 정보 가져오기
        RiderManagementDto riderManagementDto = riderManagementService.getRiderManagementInfo(userId);

        // model에 RiderManagementDto 담기
        model.addAttribute("riderManagement", riderManagementDto);
        model.addAttribute("rider",rider);

        return "/content/rider/management"; // rider/management.html로 이동
    }
}
