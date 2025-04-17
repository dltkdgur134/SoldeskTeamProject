package com.soldesk6F.ondal.user.controller.rider;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;

import com.soldesk6F.ondal.user.dto.RiderForm;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.User.UserRole;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.user.service.OwnerService;
import com.soldesk6F.ondal.user.service.RiderService;
import com.soldesk6F.ondal.user.service.UserRoleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RegRiderController {

    private final RiderService riderService;
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;

    @GetMapping("/register")
    public String showRiderRegistrationForm(@AuthenticationPrincipal CustomUserDetails userDetails,
    		RedirectAttributes redirectAttributes) {
        String userId = userDetails.getUser().getUserId();
        //이미 라이더로 등록된 user 인지 확인
        if (riderService.isAlreadyRider(userId)) {
        	redirectAttributes.addFlashAttribute("riderExists", true);
        	 return "redirect:/"; 
        }
        // 역할이 user 뿐일 때 바로 페이지 전환
        return "content/rider/riderRegister";
    }

    @PostMapping("/register")
    public String registerRider(@AuthenticationPrincipal CustomUserDetails userDetails,
        @ModelAttribute RiderForm riderForm,
        @RequestParam("secondaryPassword") String secondaryPassword,
        @RequestParam("secondaryPasswordConfirm") String secondaryPasswordConfirm,
        RedirectAttributes redirectAttributes) {
    	if (!secondaryPassword.equals(secondaryPasswordConfirm)) {
	        redirectAttributes.addFlashAttribute("error", "2차 비밀번호가 일치하지 않습니다.");
	        return "redirect:/rider/register";
	    }
    	
    	String userId = userDetails.getUser().getUserId();
     // 이미 라이더로 등록된 경우
        if (riderService.isAlreadyRider(userId)) {
            redirectAttributes.addFlashAttribute("riderExists", true);
            return "redirect:/";
        }


        
     // 🔁 4. 세션의 Authentication 갱신
        // 변경된 사용자 정보를 다시 로딩
        User updatedUser = userRepository.findByUserId(userId).orElseThrow();

        // 새로운 CustomUserDetails 생성
        CustomUserDetails updatedDetails = new CustomUserDetails(updatedUser, UserRole.valueOf(updatedUser.getUserRole().name()));

        // 새로운 Authentication 객체 생성
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
            updatedDetails, null, updatedDetails.getAuthorities()
        );

        // SecurityContext에 설정
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        
        User user = userRepository.findByUserId(userId).orElseThrow();
        userRoleService.changeRoleToRider(user, riderForm);

        redirectAttributes.addFlashAttribute("riderSuccess", "라이더 등록이 완료되었습니다!");
        return "redirect:/";
    }
}
