package com.soldesk6F.ondal.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FindIdPasswordController {

	private final FindIdPasswordService findIdPasswordService;
	
	@GetMapping("/login/findPassword")
	public String findPassword() {
		
		return "content/findPassword";
	}
	
	@PostMapping("/login/tryFindPassword")
	public String tryFindPassword(@RequestParam("id")String id ,@RequestParam("password")String password) {
//		findIdPasswordService.find
		
		
		
		
		
		
		
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
