package com.soldesk6F.ondal.controller;

import com.soldesk6F.ondal.domain.User;
import com.soldesk6F.ondal.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;

/*import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;*/
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${upload.path}")
    private String uploadDir;
    
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
            @RequestParam("userAddressDetail") String userAddressDetail,
            @RequestParam("profileImage") MultipartFile profileImage,
            @RequestParam("socialLoginProvider") String socialLoginProvider,
            Model model
    ) {
    	if (userRepository.existsById(userId)) {
    		model.addAttribute("error", "이미 등록된 id입니다.");
    		model.addAttribute("userId", userId);
    		model.addAttribute("userName", userName);
    		model.addAttribute("nickname", nickname);
    		model.addAttribute("email", email);
    		model.addAttribute("userPhone", userPhone);
    		model.addAttribute("userAddress", userAddress);
    		model.addAttribute("socialLoginProvider", socialLoginProvider);
    		return "register";
    	}
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "이미 등록된 이메일입니다.");
            model.addAttribute("userId", userId);
            model.addAttribute("userName", userName);
            model.addAttribute("nickname", nickname);
            model.addAttribute("email", email);
    		model.addAttribute("userPhone", userPhone);
    		model.addAttribute("userAddress", userAddress);
    		model.addAttribute("socialLoginProvider", socialLoginProvider);
            return "register";
        }
        
        String fileName = profileImage.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        String savePath = new File(uploadDir).getAbsolutePath();
        File saveFolder = new File(savePath);
        if (!saveFolder.exists()) {
            saveFolder.mkdirs(); // 폴더 없으면 생성
        }

        File saveFile = new File(saveFolder, fileName);
        
        try {
            profileImage.transferTo(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "파일 업로드 실패");
            model.addAttribute("userId", userId);
            model.addAttribute("userName", userName);
            model.addAttribute("nickname", nickname);
            model.addAttribute("email", email);
    		model.addAttribute("userPhone", userPhone);
    		model.addAttribute("userAddress", userAddress);
    		model.addAttribute("socialLoginProvider", socialLoginProvider);
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
                .userAddress(userAddress + " " + userAddressDetail)
                .userProfileName(fileName)
                .userProfilePath(uploadDir + File.separator + fileName)
                .userProfileExtension(extension)
                .socialLoginProvider(socialLoginProvider)
                .build();

        userRepository.save(user);

        return "redirect:/login";
    }
    
}