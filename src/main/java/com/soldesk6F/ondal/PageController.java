package com.soldesk6F.ondal;
 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
	
	// 마이페이지 이동
	@GetMapping (value = "/myPage")
	public String goMyPage(Model model) {
		System.out.println("myPage 컨트롤러 진입");
		return "content/myPage";
	}
	
	// 비밀번호 변경 페이지 이동
	@GetMapping (value = "/mySecurity")
	public String enterPass() {
		return "content/mySecurity";
	}
	
	// 회원탈퇴 안내사항 페이지 이동
	@GetMapping("/deleteUserPage")
	public String goDeleteUserPage() {
		return "content/deleteUserPage";
	}
	
	// 주소 등록 페이지 이동
	@GetMapping(value = "/regAddress")
	public String goRegAddress() {
		return "content/regAddress";
	}
	
	
}
