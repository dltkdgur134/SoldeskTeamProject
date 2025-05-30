package com.soldesk6F.ondal.user.controller.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DeleteUserController {

	private final UserService userService;
	
	// 회원 탈퇴 전 비밀번호 확인
	@PostMapping("/checkPassword")
	public String checkMyPassword(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("password") String password,
			RedirectAttributes redirectAttribute) {
		if (!userService.checkPassword(userDetails, password, redirectAttribute)) {
			return "redirect:/checkMyPass";
		}
		return "redirect:/deleteUserPage";
	}
	
	// 회원 탈퇴
	@PostMapping("/deleteUser")
	public String deleteUser(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			RedirectAttributes redirectAttribute) {
		if (!userService.deleteUserTemp(userDetails, redirectAttribute)) {
			return "redirect:/myPage";
		}
		return "redirect:/logout";
	}
	
	// 비밀번호 확인
	@GetMapping(value = "/checkMyPass")
	public String goToPassCheck(Model model) {
		model.addAttribute("checkPassPurpose", "회원탈퇴");
		return "content/myPassCheck";
	}
	
}
