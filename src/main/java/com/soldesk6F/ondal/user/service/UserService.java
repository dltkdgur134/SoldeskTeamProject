package com.soldesk6F.ondal.user.service;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${upload.path}")
    private String uploadDir;

    public boolean isUserIdDuplicate(String userId) {
        return userRepository.existsById(userId);
    }

    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean registerUser(String userId, String userName, String nickname, String email,
                             String password, String userPhone, String userAddress, String userAddressDetail,
                             MultipartFile profileImage, String socialLoginProvider) {
    	try {

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
	            profileImage.transferTo(saveFile);
	            filePath = uploadDir + File.separator + fileName;
	        }
	
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
	                .userProfileExtension(extension)
	                .userProfilePath(filePath)
	                .socialLoginProvider(socialLoginProvider)
	                .build();
	
	        userRepository.save(user);
	        return true;
    	} catch (IOException e) {
            e.printStackTrace();
            return false;
        }
	}
}