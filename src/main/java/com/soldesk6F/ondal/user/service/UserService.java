package com.soldesk6F.ondal.user.service;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.regAddress.entity.RegAddress;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
	
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${upload.path}")
    private String uploadDir;
    public Optional<User> findUserByUuid(UUID userUuid) {
    	return userRepository.findByUserUuid(userUuid);
    }

    public boolean isUserIdDuplicate(String userId) {
        return userRepository.existsByUserId(userId);
    }

    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean isPhoneDuplicate(String userPhone) {
        return userRepository.existsByUserPhone(userPhone);
    }

    public boolean registerUser(String userId, String userName, String nickname, String email,
                             String password, String userPhone, RegAddress userSelectedAddress,
                             MultipartFile profileImage, String socialLoginProvider) {
    	try {

	        String fileName = "default.png";
	        String extension = "png";
	        String filePath = uploadDir + File.separator + fileName;
	
	        if (profileImage != null && !profileImage.isEmpty()) {
	        	String originalFilename = profileImage.getOriginalFilename();
	        	extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
	        	fileName = userId + "_" + System.currentTimeMillis() + "." + extension;
	            String savePath = new File(uploadDir).getAbsolutePath(); // getRealPath()
	
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
	                .userSelectedAddress(userSelectedAddress)	
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