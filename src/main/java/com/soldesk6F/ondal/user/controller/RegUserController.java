package com.soldesk6F.ondal.user.controller;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
@RequiredArgsConstructor
public class RegUserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${upload.path}")
    private String uploadDir;
    
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
        ////////////////////////////////////////////////////////////////////
        
    	String fileName = "default.png";
    	String extension = "png";
    	String filePath = uploadDir + File.separator + fileName;

    	if (profileImage != null && !profileImage.isEmpty()) {
    	    fileName = profileImage.getOriginalFilename();
    	    extension = fileName.substring(fileName.lastIndexOf(".") + 1);

    	    String savePath = new File(uploadDir).getAbsolutePath();
    	    File saveFolder = new File(savePath);
    	    if (!saveFolder.exists()) {
    	        saveFolder.mkdirs();
    	    }

    	    File saveFile = new File(saveFolder, fileName);
    	    try {
    	        profileImage.transferTo(saveFile);
    	        filePath = uploadDir + File.separator + fileName;
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	        model.addAttribute("error", "파일 업로드 실패");
    	        return "register";
    	    }
    	}
        

        //////////////////////////////////////////////////////////////////
        String encryptedPassword = passwordEncoder.encode(password);
        
        User user = User.builder()
                .userId(userId)
                .userName(userName)
                .nickName(nickname)
                .email(email)
                .password(encryptedPassword)
                .userPhone(userPhone)
                .userAddress(userAddress + " " + userAddressDetail)
                .userProfileName(fileName)
//                .userProfilePath(uploadDir + File.separator + fileName)
                .userProfilePath(filePath)
                .userProfileExtension(extension)
                .socialLoginProvider(socialLoginProvider)
                .build();

        userRepository.save(user);

        return "redirect:/login";
    }
    
}