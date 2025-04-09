package com.soldesk6F.ondal.user.controller;

import com.soldesk6F.ondal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class RegUserController {

    private final UserService userService;


    @GetMapping("/register")
    public String showRegisterForm() {
        return "content/register";
    }

    @GetMapping("/regAgreement")
    public String showRegAgreementForm() {
        return "content/regAgreement";
    }

    @PostMapping("/content/register")
    public String register(
            @RequestParam("userId") String userId,
            @RequestParam("userName") String userName,
            @RequestParam("nickname") String nickname,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("userPhone") String userPhone,
            @RequestParam("userAddress") String userAddress,
            @RequestParam("userAddressDetail") String userAddressDetail,
            @RequestParam("profileImage") MultipartFile profileImage,
            @RequestParam(value = "socialLoginProvider", required = false) String socialLoginProvider,
            Model model
    ) {
        if (userService.isUserIdDuplicate(userId)) {
            model.addAttribute("error", "이미 등록된 id입니다.");
            fillUserData(model, userId, userName, nickname, email, userPhone, userAddress, userAddressDetail, socialLoginProvider);
            return "content/register";
        }

        if (userService.isEmailDuplicate(email)) {
            model.addAttribute("error", "이미 등록된 이메일입니다.");
            fillUserData(model, userId, userName, nickname, email, userPhone, userAddress, userAddressDetail, socialLoginProvider);
            return "content/register";
        }
        
        if (userService.isPhoneDuplicate(userPhone)) {
            model.addAttribute("error", "이미 등록된 전화번호입니다.");
            fillUserData(model, userId, userName, nickname, email, userPhone, userAddress, userAddressDetail, socialLoginProvider);
            return "content/register";
        }

        boolean success = userService.registerUser(
                userId, userName, nickname, email, password,
                userPhone, userAddress, userAddressDetail,
                profileImage, socialLoginProvider
        );

        if (!success) {
            fillUserData(model, userId, userName, nickname, email, userPhone, userAddress, userAddressDetail, socialLoginProvider);
            return "content/register";
        }

        return "redirect:/login";
    }

    private void fillUserData(Model model,
                              String userId, String userName, String nickname,
                              String email, String userPhone, String userAddress,
                              String userAddressDetail, String socialLoginProvider) {
        model.addAttribute("userId", userId);
        model.addAttribute("userName", userName);
        model.addAttribute("nickname", nickname);
        model.addAttribute("email", email);
        model.addAttribute("userPhone", userPhone);
        model.addAttribute("userAddress", userAddress);
        model.addAttribute("userAddressDetail", userAddressDetail);
        model.addAttribute("socialLoginProvider", socialLoginProvider);
        if (email != null && email.contains("@")) {
            String[] parts = email.split("@", 2);
            model.addAttribute("emailId", parts[0]);
            model.addAttribute("emailDomain", parts[1]);
        }
    }
}