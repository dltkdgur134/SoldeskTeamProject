package com.soldesk6F.ondal.user.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

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
    
    public boolean isPhoneDuplicate(String userPhone) {
        return userRepository.existsByUserPhone(userPhone);
    }

    public boolean registerUser(String userId, String userName, String nickname, String email,
                             String password, String userPhone, String userAddress, String userAddressDetail,
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
	                .userAddress(userAddress + " " + userAddressDetail)	
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
    
    @Transactional
    public boolean updateUserNickname(String nickName, User user, Model model) {
    	Optional<User> findUser = userRepository.findByUserId(user.getUserId());
    	if (findUser.get().getNickName().equals(nickName)) {
//    		throw new IllegalArgumentException("기존 닉네임과 동일합니다.");
    		return false;
    	} else {
    		findUser.ifPresent(U -> U.setNickName(nickName) );
    		findUser.ifPresent(U -> U.setUpdatedDate(LocalDateTime.now()) );
    		return true;
    	}
    	
//    	findUser.ifPresent(value -> value.setNickName(nickName));
    }

    
    
    
}

