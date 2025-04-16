package com.soldesk6F.ondal.email;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/email")
public class EmailController {

	@Autowired
	private EmailService emailService;

	@GetMapping("/send")
	public String sendVerificationEmail(@RequestParam("email") String email, HttpSession session) {
    	String code;
		try {
			code = emailService.sendVerificationMailWithCid(email);
			session.setAttribute("emailVerificationCode", code);
			session.setAttribute("emailCodeTime", System.currentTimeMillis());
		} catch (IOException e) {
			e.printStackTrace();
		}
        return "인증 메일이 전송되었습니다.";
	}

	@GetMapping("/verify")
	public String verifyCode(@RequestParam("code") String code, HttpSession session) {
		try {
			System.out.println("=== [이메일 인증 요청] ===");
		    System.out.println("입력한 코드: " + code);
			String storedCode = (String) session.getAttribute("emailVerificationCode");
			Long savedTime = (Long) session.getAttribute("emailCodeTime");
		    System.out.println("저장된 코드: " + storedCode);
			if (storedCode == null || savedTime == null) {
		        return "fail";
			}
	
			long currentTime = System.currentTimeMillis();
			if (currentTime - savedTime > 2 * 60 * 1000) {
		    	return "expired";
		    }
	
		    if (code.equals(storedCode)) {
		    	session.removeAttribute("emailVerificationCode");
		        session.removeAttribute("emailCodeTime");
		        return "ok";
			} else {
		        return "fail";
			}
		} catch (Exception e) {
	        e.printStackTrace();
	        return "fail";
	    }
	}
}