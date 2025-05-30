package com.soldesk6F.ondal.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soldesk6F.ondal.admin.entity.Admin;
import com.soldesk6F.ondal.admin.repository.AdminRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminLoginController {
	
	private final AdminRepository adminRepository;
	
	@GetMapping("/login")
	public String showAdminLoginForm() {
		return "content/admin/adminLogin";
	}
	
	@PostMapping("/login")
	public String adminLogin(@RequestParam("loginId") String loginId,
	                         @RequestParam("password") String password,
	                         HttpSession session,
	                         Model model) {
		Admin admin = adminRepository.findById(loginId).orElse(null);
		if (admin == null || !admin.getPassword().equals(password)) {
			model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
			return "content/admin/adminLogin";
		}
		
		// 로그인 성공시 세션에 admin 저장
		session.setAttribute("adminLogin", admin);

		return "redirect:/admin/home";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate(); // 로그아웃시 세션 제거
		return "redirect:/admin/login";
	}
	
}
