package com.soldesk6F.ondal.user.controller.owner;


import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.repository.OwnerRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerHomeController {
	
	private final OwnerRepository ownerRepository;

    @GetMapping("/home")
    public String ownerHomeGet(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
         UUID userUuid = userDetails.getUser().getUserUuid();

        Optional<Owner> optionalOwner = ownerRepository.findByUser_UserUuid(userUuid);

        if (optionalOwner.isPresent()) {
            Owner owner = optionalOwner.get();

            // 디버깅 로그
            System.out.println("✅ Owner 불러오기 성공!");
            System.out.println("🆔 ownerId: " + owner.getOwnerId());
            System.out.println("📅 등록일: " + owner.getRegistrationDate());

            model.addAttribute("owner", owner);
        } else {
            System.out.println("❌ Owner 정보 없음. userId = " + userUuid);
        }

        return "content/owner/ownerHome"; // templates/content/owner/ownerHome.html
    }

    @GetMapping("/infopage")
    public String showMyPage() {
        return "redirect:/infopage"; // templates/user/infopage.html
    }

    @GetMapping("/ownerInfopage")
    public String showOwnerMyPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
    	UUID userUuid = userDetails.getUser().getUserUuid();

        Optional<Owner> optionalOwner = ownerRepository.findByUser_UserUuid(userUuid);

        if (optionalOwner.isPresent()) {
            Owner owner = optionalOwner.get();
            model.addAttribute("owner", owner);
        } else {
        	System.out.println("Owner 정보 없음. userUuid = " + userUuid);
            // 예외 처리 또는 에러 페이지 이동
        }

        return "content/owner/ownerInfopage"; // templates/content/owner/ownerInfopage.html
    }
}
