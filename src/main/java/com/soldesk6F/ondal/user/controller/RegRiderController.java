package com.soldesk6F.ondal.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String showRiderRegistrationForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model
    		,@ModelAttribute("riderExists") String riderExists,RedirectAttributes redirectAttributes) {
        String userId = userDetails.getUser().getUserId();

        if (riderService.isAlreadyRider(userId)) {
        	 //model.addAttribute("riderExists", riderExists); // 모델에 넣어줘야 Thymeleaf가 사용 가능
        	 redirectAttributes.addAttribute("riderExists", riderExists);
        	 return "redirect:/"; // templates/content/index.html
        }

        model.addAttribute("riderForm", new RiderForm());
        return "content/rider_register";
    }

    @PostMapping("/register")
    public String registerRider(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @ModelAttribute RiderForm riderForm,
                                RedirectAttributes redirectAttributes) {
        String userId = userDetails.getUser().getUserId();
     // 이미 라이더로 등록된 경우
        if (riderService.isAlreadyRider(userId)) {
            redirectAttributes.addFlashAttribute("riderExists", true);
            return "redirect:/";
        }
        User user = userRepository.findByUserId(userId).orElseThrow();

        riderService.registerRider(user, riderForm);

        user.setUserRole(UserRole.RIDER);
        userRepository.save(user);

        return "redirect:/";
    }
}
