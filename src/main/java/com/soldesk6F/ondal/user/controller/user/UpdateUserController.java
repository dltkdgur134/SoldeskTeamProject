package com.soldesk6F.ondal.user.controller.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.user.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UpdateUserController {
	
	private final UserService userService;

	@PostMapping("/content/updateNickname")
	public String updateNickname(
			@RequestParam("nickname") String nickName, 
			Model model,
			RedirectAttributes redirectAttr) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = customUserDetails.getUser();
		
		if (!userService.updateUserNickname(nickName, user, model)) {
			model.addAttribute("success", "닉네임을 변경할 수 없습니다.");
			model.addAttribute("nicknameError", true);
			return "redirect:/infopage";
		}
		customUserDetails.getUser().setNickName(nickName);
		redirectAttr.addFlashAttribute("success", "닉네임 변경 성공!");
		return "redirect:/infopage";
	}
	
	@PostMapping("/content/updateProfilePic")
	public String updateProfilePic(
			@RequestParam("userProfilePath") String userProfilePath, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = customUserDetails.getUser();
		
		
		return "redirect:/infopage";
	}
	
	
	
}

//@PostMapping("/content/updateNickname")
//public String updateNickname(
//		@RequestParam("nickname") String nickName, 
//		Model model,
//		RedirectAttributes redirectAttr) {
//	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//	CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
//	User user = customUserDetails.getUser();
//	
//	if (!userService.updateUserNickname(nickName, user, model)) {
//		model.addAttribute("success", "닉네임에 변경 사항이 없습니다.");
//		model.addAttribute("nicknameError", true);
//		return "redirect:/infopage";
//	}
//	customUserDetails.getUser().setNickName(nickName);
//	redirectAttr.addFlashAttribute("success", "Nickname updated successfully!");
//	return "redirect:/infopage";
//}


