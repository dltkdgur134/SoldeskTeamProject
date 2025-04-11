package com.soldesk6F.ondal.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.user.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UpdateUserController {

    private final UserRepository userRepository;
	
	private final UserService userService;

	@PostMapping("/content/updateNickname")
	public String updateNickname(
			@RequestParam("nickname") String nickName, 
			Model model,
			RedirectAttributes rAttr) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = customUserDetails.getUser();
		
		if (!userService.updateUserNickname(nickName, user, model)) {
			model.addAttribute("success", "닉네임을 변경할 수 없습니다.");
			model.addAttribute("nicknameError", true);
			return "redirect:/infopage";
		}
		customUserDetails.getUser().setNickName(nickName);
		rAttr.addFlashAttribute("success", "닉네임 변경 성공!");
		return "redirect:/infopage";
	}
	
	@PostMapping("/content/updateProfilePic")
	public String updateProfilePic(
			@RequestParam("profileImage") MultipartFile profileImage, Model model,
			RedirectAttributes rAttr) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = customUserDetails.getUser();
		
		if (!userService.updateUserPicture(user, profileImage, model)) {
			model.addAttribute("success", "프로필 이미지를 변경할 수 없습니다.");
			model.addAttribute("profilePicError", true);
			return "redirect:/infopage";
		}
		customUserDetails.getUser().setUserProfilePath(userRepository.findByUserId(user.getUserId()).get().getUserProfilePath());
		model.addAttribute("success", "프로필 이미지 변경 성공!");
		rAttr.addFlashAttribute("redirected", true);
		return "redirect:/infopage";
	}
	
	
}

