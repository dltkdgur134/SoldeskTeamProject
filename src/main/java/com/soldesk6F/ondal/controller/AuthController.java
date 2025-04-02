package com.soldesk6F.ondal.controller;

import com.soldesk6F.ondal.domain.User;
import com.soldesk6F.ondal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

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
            @RequestParam("userName") String userName,
            @RequestParam("nickname") String nickname,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("userPhone") String userPhone,
            @RequestParam("userAddress") String userAddress,
            @RequestParam("userProfileName") String userProfileName,
            @RequestParam("userProfilePath") String userProfilePath,
            @RequestParam("userProfileExtension") String userProfileExtension,
            @RequestParam("socialLoginProvider") String socialLoginProvider,
            @RequestParam("username") String username,
            Model model
    ) {
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "이미 등록된 이메일입니다.");
            return "register";
        }
        
//        String encryptedPassword = passwordEncoder.encode(password);
        
        User user = User.builder()
                .userId(userId)
                .userName(userName)
                .nickname(nickname)
                .email(email)
                .password(password)
                .userPhone(userPhone)
                .userAddress(userAddress)
                .userProfileName(userProfileName)
                .userProfilePath(userProfilePath)
                .userProfileExtension(userProfileExtension)
                .socialLoginProvider(socialLoginProvider)
                .username(username)
                .build();

        userRepository.save(user);

        return "redirect:/login";
    }
    
}