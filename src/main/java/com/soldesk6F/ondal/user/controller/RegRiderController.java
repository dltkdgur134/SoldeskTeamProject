package com.soldesk6F.ondal.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk6F.ondal.user.CustomUserDetails;
import com.soldesk6F.ondal.user.dto.RiderForm;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.User.UserRole;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.user.service.RiderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RegRiderController {

    private final RiderService riderService;
    private final UserRepository userRepository;

    @GetMapping("/register")
    public String showRiderRegistrationForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String userId = userDetails.getUser().getUserId();

        if (riderService.isAlreadyRider(userId)) {
            return "redirect:/mypage";
        }

        model.addAttribute("riderForm", new RiderForm());
        return "content/rider_register";
    }

    @PostMapping("/register")
    public String registerRider(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @ModelAttribute RiderForm riderForm) {
        String userId = userDetails.getUser().getUserId();

        User user = userRepository.findById(userId).orElseThrow();

        riderService.registerRider(user, riderForm);

        user.setUserRole(UserRole.RIDER);
        userRepository.save(user);

        return "redirect:/mypage";
    }
}
