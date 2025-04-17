package com.soldesk6F.ondal.user.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.soldesk6F.ondal.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DeleteUserController {

	private final UserService userService;
	
	@DeleteMapping("/deleteUser")
	public String deleteUser() {
		
		
		return "content/index";
	}
	
	
	
	
	
}
