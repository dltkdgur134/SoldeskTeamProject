package com.soldesk6F.ondal.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.User.Status;
import com.soldesk6F.ondal.user.repository.UserRepository;

@Controller
@RequiredArgsConstructor
public class RegUserController {

    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder passwordEncoder;

    // 회원가입 폼 보여주기
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // templates/register.html로 연결
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String register(
            @RequestParam("userId") String userId,
            @RequestParam("password") String password,
            @RequestParam("userProfileName") String userProfileName,
            @RequestParam("userProfileExtension") String userProfileExtension,
            @RequestParam("userProfilePath") String userProfilePath,
            @RequestParam("userName") String userName,
            @RequestParam("nickname") String nickname,
            @RequestParam("email") String email,
            @RequestParam("userPhone") String userPhone,
            @RequestParam("userAddress") String userAddress,
            @RequestParam("socialLoginProvider") String socialLoginProvider,
            Model model
    ) 
     
    {
        if (userRepository.existsById(userId)) {
        	model.addAttribute("error", "이미 등록된 ID입니다.");
            return "register";
        }
    	
    	
    	
    	if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "이미 등록된 이메일입니다.");
            return "register";
        }
        
//        String encryptedPassword = passwordEncoder.encode(password);
        
        User user = User.builder()
                .userId(userId)
                .userName(userName)
                .nickName(nickname)
                .email(email)
                .password(password)
                .userPhone(userPhone)
                .userAddress(userAddress)
                .userProfileName(userProfileName)
                .userProfilePath(userProfilePath)
                .userProfileExtension(userProfileExtension)
                .socialLoginProvider(socialLoginProvider)
                .build();

        userRepository.save(user);

        return "redirect:/login";
    }
    
}