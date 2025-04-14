package com.soldesk6F.ondal.user.service;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import com.soldesk6F.ondal.useract.regAddress.entity.RegAddress;

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
    
    @Transactional
    public boolean updateUserNickname(String nickName, User user, Model model) {
    	Optional<User> findUser = userRepository.findByUserId(user.getUserId());
    	
    	if (findUser.isEmpty()) {
    		throw new IllegalArgumentException("존재하지 않는 아이디 입니다.");
    	}
    	
    	User currentUser = findUser.get();
    	
   		if (currentUser.getNickName().equals(nickName)) {
    		model.addAttribute("error", "기존 닉네임과 동일합니다.");
    		return false;
    	} else {
//    		findUser.ifPresent(U -> U.setNickName(nickName) );
//    		findUser.ifPresent(U -> U.setUpdatedDate(LocalDateTime.now()));
    		currentUser.setNickName(nickName);
    		currentUser.setUpdatedDate(LocalDateTime.now());
    		return true;
    	}
    }
    
    
    @Transactional
    public boolean updateUserPicture(User user, MultipartFile profileImage, Model model) {
    	Optional<User> findUser = userRepository.findByUserId(user.getUserId());
    		
   		try {
    		String old_profImgPath = findUser.get().getUserProfilePath(); 
    		String old_profImgName = old_profImgPath.split("\\\\")[1];
    		String oldSavePath = new File(uploadDir).getAbsolutePath();
    		Path oldImgPath = Paths.get(oldSavePath, old_profImgName);
    		//System.out.println(oldImgPath.toString());
    		if (Files.exists(oldImgPath)) {
    			Files.delete(oldImgPath);
    		}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "존재하지 않는 파일입니다.");
		}
    	
    	try {
    		String fileName = "default.png";
	        String extension = "png";
	        String filePath = uploadDir + File.separator + fileName;
	
	        if (profileImage != null && !profileImage.isEmpty()) {
	        	String originalFilename = profileImage.getOriginalFilename();
	        	extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
	        	fileName = findUser.get().getUserId() + "_" + System.currentTimeMillis() + "." + extension;
//	        	fileName = findUser.get().getUserUuidAsString() + "." + extension;
	            String savePath = new File(uploadDir).getAbsolutePath(); // getRealPath()
	            System.out.println(savePath);
	            File saveFolder = new File(savePath);
	            if (!saveFolder.exists()) {
	                saveFolder.mkdirs();
	            }
	
	            File saveFile = new File(saveFolder, fileName);
	            profileImage.transferTo(saveFile);
	            filePath = uploadDir + File.separator + fileName;
	            filePath.replace("/", File.separator);
	            
	            final String finalFilePath = new String(filePath);
//	            final String finalFileName = new String(fileName);
	            findUser.ifPresent(U -> U.setUserProfilePath(finalFilePath));
//	            findUser.ifPresent(U -> U.setUserProfilePath(finalFileName));
	            findUser.ifPresent(U -> U.setUpdatedDate(LocalDateTime.now()));
	            return true;
	        }
	        return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    	
    }
    
    @Transactional
    public boolean updateUserPhone(String userPhone, User user, Model model) {
    	Optional<User> findUser = userRepository.findByUserId(user.getUserId());
    	
    	if (findUser.isEmpty()) {
    		throw new IllegalArgumentException("존재하지 않는 아이디 입니다.");
    	}
    	
    	User currentUser = findUser.get();
    	
   		if (currentUser.getUserPhone().equals(userPhone)) {
    		model.addAttribute("error", "기존 전화번호와 동일합니다.");
    		return false;
    	} else {
//    		findUser.ifPresent(U -> U.setNickName(nickName) );
//    		findUser.ifPresent(U -> U.setUpdatedDate(LocalDateTime.now()));
    		currentUser.setUserPhone(userPhone);
    		currentUser.setUpdatedDate(LocalDateTime.now());
    		return true;
    	}
    }
    
    
    
}

