package com.soldesk6F.ondal.user.controller.owner;

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
import com.soldesk6F.ondal.user.dto.OwnerForm;
import com.soldesk6F.ondal.user.dto.RiderForm;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.User.UserRole;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.user.service.OwnerService;
import com.soldesk6F.ondal.user.service.RiderService;
import com.soldesk6F.ondal.user.service.UserRoleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class RegOwnerController {
	private final OwnerService ownerService;
	private final UserRepository userRepository;
	private final UserRoleService userRoleService;
	
	
	@GetMapping("/register")
	public String showOwnerRegistratinForm(@AuthenticationPrincipal CustomUserDetails userDetails,
			RedirectAttributes redirectAttributes) {
		String userId = userDetails.getUser().getUserId();
		//이미 점주로 등록된 user 인지 확인
		if (ownerService.isAlreadyOwner(userId)) {
        	redirectAttributes.addFlashAttribute("ownerExists", true);
        	 return "redirect:/"; 
        }
		// 역할이 user 뿐일 때 바로 페이지 전환
		 return "content/owner/ownerRegister";
	}
	
	
	
	@PostMapping("/ownerRegGoToStoreReg")
	public String showStoreRegistrationPage(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@ModelAttribute OwnerForm ownerForm,
		@RequestParam("ownerNickname") String ownerNickname,
		@RequestParam("secondaryPassword") String secondaryPassword,
		@RequestParam("secondaryPasswordConfirm") String secondaryPasswordConfirm,
	    RedirectAttributes redirectAttributes) {
		 // 확인 비밀번호 비교 로직 추가 가능
	    if (!secondaryPassword.equals(secondaryPasswordConfirm)) {
	        redirectAttributes.addFlashAttribute("error", "2차 비밀번호가 일치하지 않습니다.");
	        return "redirect:/owner/register";
	    }
	    String userId = userDetails.getUser().getUserId();
	    // 이미 점주인지 아닌지 확인
	    if (ownerService.isAlreadyOwner(userId)) {
        	redirectAttributes.addFlashAttribute("ownerExists", true);
        	 return "redirect:/"; 
        
	    }
	    // 바로 점주 등록 (암호화는 서비스 내부에서 처리)
        
	    
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
        redirectAttributes.addFlashAttribute("ownerSuccess", "점주 등록이 완료되었습니다!");
        
        User user = userRepository.findByUserId(userId).orElseThrow();
        userRoleService.changeRoleToOwner(user, ownerForm);
	    // 가게 등록 페이지로 이동
//	    return "redirect:/owner/content/storereg/submit";
	    return "redirect:/storeReg/submit";
	    
	}
	
	@GetMapping("/content/storereg/submit")
	public String showSubmitPage() {
	    return "content/storereg/submit";  // submit.html 파일을 Thymeleaf 템플릿으로 처리
	}
	
	
	
	
	
	
	
	

}
