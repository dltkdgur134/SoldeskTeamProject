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
	
	@GetMapping(value = "/checkMyPass")
	public String goToPassCheck(Model model) {
		model.addAttribute("checkPassPurpose", "회원탈퇴");
		return "content/myPassCheck";
	}
	
	@PostMapping("/checkPassword")
	public String checkMyPassword(
			@AuthenticationPrincipal CustomUserDetails cud,
			@RequestParam("password") String password,
			RedirectAttributes rAttr) {
		if (!userService.checkPassword(cud, password, rAttr)) {
			return "redirect:/checkMyPass";
		}
		return "redirect:/deleteUserPage";
	}
	
	@GetMapping("/deleteUserPage")
	public String goDeleteUserPage() {
		return "content/deleteUserPage";
	}
	
	@PostMapping("/deleteUser")
	public String deleteUser(
			@AuthenticationPrincipal CustomUserDetails cud,
			RedirectAttributes rAttr) {
		if (!userService.deleteUserTemp(cud, rAttr)) {
			return "redirect:/myPage";
		}
		return "redirect:/logout";
	}
	
}
