package com.soldesk6F.ondal.user.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.login.CustomUserDetailsService;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.User.UserStatus;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.repository.OwnerRepository;
import com.soldesk6F.ondal.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import com.soldesk6F.ondal.useract.regAddress.entity.RegAddress;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

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
    
    public boolean isNicknameDuplicate(String nickName) {
    	return userRepository.existsByNickName(nickName);
    }

    public boolean registerUser(String userId, String userName, String nickname, String email,
                             String password, String userPhone, RegAddress userSelectedAddress,
                             MultipartFile profileImage, String socialLoginProvider) {
    	try {

	        String fileName = "default.png";
	        String extension = "png";
//	        String filePath = uploadDir + File.separator + fileName;
	        
//	        String webPath = "/img/userProfiles/" + fileName;
	        String webPath = fileName;
	
	        if (profileImage != null && !profileImage.isEmpty()) {
	        	String originalFilename = profileImage.getOriginalFilename();
	        	extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
	        	fileName = userId + "_" + System.currentTimeMillis() + "." + extension;
	            String savePath = new File(uploadDir).getAbsolutePath();
	
	            File saveFolder = new File(savePath);
	            if (!saveFolder.exists()) {
	                saveFolder.mkdirs();
	            }
	
	            File saveFile = new File(saveFolder, fileName);
	            profileImage.transferTo(saveFile);
//	            filePath = uploadDir + File.separator + fileName;
//	            webPath = "/img/userProfiles/" + fileName;
	            webPath = fileName;
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
//	                .userProfile(filePath)
//	                .userProfile(fileName)
	                .userProfile(webPath)
	                .socialLoginProvider(socialLoginProvider)
	                .build();
	
	        userRepository.save(user);
	        return true;
    	} catch (IOException e) {
            e.printStackTrace();
            return false;
        }
	}
    
    // 유저 정보 업데이트 시 authentication refresh
    public void refreshUserAuthentication(String userId) {
    	 CustomUserDetails updatedUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(userId);
    	 UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
    			 updatedUserDetails,
    			 updatedUserDetails.getPassword(),
    			 updatedUserDetails.getAuthorities()
    			 );
    	 SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
    
    // 유저 닉네임 업데이트
    @Transactional
    public boolean updateUserNickname(CustomUserDetails userDetails, 
    		String nickName, 
    		RedirectAttributes redirectAttributes) {
    	String userUUIDString = userDetails.getUser().getUserUuidAsString();
    	UUID userUuid = UUID.fromString(userUUIDString);
    	Optional<User> findUser = userRepository.findById(userUuid);
    	
    	if (findUser.isEmpty()) {
    		redirectAttributes.addFlashAttribute("result", 1);
    		redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
    		return false;
    	}
   		if (findUser.get().getNickName().equals(nickName)) {
   			redirectAttributes.addFlashAttribute("result", 1);
   			redirectAttributes.addFlashAttribute("resultMsg", "닉네임을 변경할 수 없습니다.");
    		return false;
    	} else {
    		findUser.get().updateNickname(nickName);
    		findUser.get().updateUpdatedDate(LocalDateTime.now());
    		refreshUserAuthentication(findUser.get().getUserId());
    		redirectAttributes.addFlashAttribute("result", 0);
    		redirectAttributes.addFlashAttribute("resultMsg", "닉네임 변경 성공!");
    		return true;
    	}
    }
    
    // 유저 프로필 이미지 업데이트
    @Transactional
    public boolean updateUserPicture(CustomUserDetails userDetails, 
    		MultipartFile profileImage, 
    		RedirectAttributes redirectAttributes) {
    	String userUUIDString = userDetails.getUser().getUserUuidAsString();
    	UUID userUuid = UUID.fromString(userUUIDString);
    	Optional<User> findUser = userRepository.findById(userUuid); 
    		
    	if (findUser.isEmpty()) {
    		redirectAttributes.addFlashAttribute("result", 1);
    		redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
    		return false;
    	}
    	
    	// 기존 이미지 존재 시 해당 파일 삭제
   		try {
   			String old_profImgName = findUser.get().getUserProfile();
//    		String old_profImgName = old_profImgPath.split("\\\\")[1];
   			if (!old_profImgName.equals("default.png")) {
   				String oldSavePath = new File(uploadDir).getAbsolutePath();
   				Path oldImgPath = Paths.get(oldSavePath, old_profImgName);
   				if (Files.exists(oldImgPath)) {
   					Files.delete(oldImgPath);
   				}
   			}
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "파일이 존재하지 않습니다.");
			return false;
		}
    	
    	try {
    		String fileName = "default.png";
	        String extension = "png";
	        String filePath = uploadDir + File.separator + fileName;
	
	        if (profileImage != null && !profileImage.isEmpty()) {
	        	String originalFilename = profileImage.getOriginalFilename();
	        	extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
	        	fileName = findUser.get().getUserId() + "_" + System.currentTimeMillis() + "." + extension;
	            String savePath = new File(uploadDir).getAbsolutePath(); // getRealPath()
//	            System.out.println(savePath);
	            File saveFolder = new File(savePath);
	            if (!saveFolder.exists()) {
	                saveFolder.mkdirs();
	            }
	
	            File saveFile = new File(saveFolder, fileName);
	            profileImage.transferTo(saveFile);
	            filePath = uploadDir + File.separator + fileName;
	            filePath.replace("/", File.separator);
	            
	            final String finalFileName = new String(fileName);
	            findUser.get().updateProfile(finalFileName);
	            findUser.get().updateUpdatedDate(LocalDateTime.now());
	            refreshUserAuthentication(findUser.get().getUserId());
	            redirectAttributes.addFlashAttribute("result", 0);
	            redirectAttributes.addFlashAttribute("resultMsg", "프로필 이미지 변경 성공!");
	            return true;
	        }
	        // 업로드한 파일 없을 시에는 기본 이미지로 설정
	        final String finalFileName = new String(fileName);
	        findUser.get().updateProfile(finalFileName);
	        findUser.get().updateUpdatedDate(LocalDateTime.now());
	        refreshUserAuthentication(findUser.get().getUserId());
	        redirectAttributes.addFlashAttribute("result", 0);
	        redirectAttributes.addFlashAttribute("resultMsg", "기본 이미지로 변경 성공!");
	        return true;
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "프로필 이미지 변경 실패!");
			return false;
		}
    }
    
    // 유저 전화번호 업데이트
    @Transactional
    public boolean updateUserPhone(CustomUserDetails userDetails, 
    		String userPhone, 
    		RedirectAttributes redirectAttributes) {
    	String userUUIDString = userDetails.getUser().getUserUuidAsString();
    	UUID userUuid = UUID.fromString(userUUIDString);
    	Optional<User> findUser = userRepository.findById(userUuid);
    	
    	if (findUser.isEmpty()) {
    		redirectAttributes.addFlashAttribute("result", 1);
    		redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
			return false;
    	}
   		if (findUser.get().getUserPhone().equals(userPhone)) {
   			redirectAttributes.addFlashAttribute("result", 1);
   			redirectAttributes.addFlashAttribute("resultMsg", "전화번호 변경 사항이 없습니다.");
    		return false;
    	} else {
    		findUser.get().updatePhone(userPhone);
    		findUser.get().updateUpdatedDate(LocalDateTime.now());
    		refreshUserAuthentication(findUser.get().getUserId());
    		redirectAttributes.addFlashAttribute("result", 0);
    		redirectAttributes.addFlashAttribute("resultMsg", "전화번호 변경 성공!");
    		return true;
    	}
    }
    
    // 유저 비밀번호 업데이트
    @Transactional
    public boolean updatePassword(CustomUserDetails userDetails, 
    		String oldPassword, 
    		String password, 
    		RedirectAttributes redirectAttributes) {
//    	Optional<User> findUser = userRepository.findByUserId(cud.getUsername());
    	String userUUIDString = userDetails.getUser().getUserUuidAsString();
    	UUID userUuid = UUID.fromString(userUUIDString);
    	Optional<User> findUser = userRepository.findById(userUuid);
    	
    	if (findUser.isEmpty()) {
    		redirectAttributes.addFlashAttribute("result", 1);
    		redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
    		return false;
    	}
    	
    	try {
   			if (passwordEncoder.matches(oldPassword, findUser.get().getPassword())) {
    			if (password.equals(oldPassword)) {
    				redirectAttributes.addFlashAttribute("result", 1);
    				redirectAttributes.addFlashAttribute("resultMsg", "비밀번호 변경사항이 없습니다." );
    				return false;
    			}
    			String encryptedPassword = passwordEncoder.encode(password);
    			findUser.get().updatePassword(encryptedPassword);
    			findUser.get().updateUpdatedDate(LocalDateTime.now());
    			redirectAttributes.addFlashAttribute("result", 0);
    			redirectAttributes.addFlashAttribute("resultMsg", "비밀번호 변경 성공!");
    			return true;
    		}
   			redirectAttributes.addFlashAttribute("result", 1);
   			redirectAttributes.addFlashAttribute("resultMsg", "기존 비밀번호가 일치하지 않습니다.");
    		return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    
    // 비밀번호 확인
    @Transactional(readOnly = true)
    public boolean checkPassword( 
    		CustomUserDetails userDetails,
    		String password,
    		RedirectAttributes redirectAttributes) {
    	String userUUIDString = userDetails.getUser().getUserUuidAsString();
    	UUID userUuid = UUID.fromString(userUUIDString);
    	Optional<User> findUser = userRepository.findById(userUuid);
    	
    	if (findUser.isEmpty()) {
    		redirectAttributes.addFlashAttribute("result", 1);
    		redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
    		return false;
    	}
    	if (passwordEncoder.matches(password, findUser.get().getPassword())) {
    		redirectAttributes.addFlashAttribute("result", 0);
    		redirectAttributes.addFlashAttribute("resultMsg", "비밀번호가 맞습니다!");
			return true;
    	}
    	redirectAttributes.addFlashAttribute("result", 1);
    	redirectAttributes.addFlashAttribute("resultMsg", "비밀번호가 틀렸습니다.");
    	return false;
    }
    
    // 유저 탈퇴 상태로 변경
    @Transactional
    public boolean deleteUserTemp(CustomUserDetails userDetails, 
    		RedirectAttributes redirectAttributes) {
    	String userUUIDString = userDetails.getUser().getUserUuidAsString();
    	UUID userUuid = UUID.fromString(userUUIDString);
    	Optional<User> findUser = userRepository.findById(userUuid);
    	
    	if (findUser.isEmpty()) {
    		redirectAttributes.addFlashAttribute("result", 1);
    		redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
			return false;
    	}
    	try {
    		findUser.get().setUserStatus(UserStatus.LEAVED);
    		findUser.get().updateUpdatedDate(LocalDateTime.now());
    		refreshUserAuthentication(findUser.get().getUserId());
    		redirectAttributes.addFlashAttribute("result", 0);
    		redirectAttributes.addFlashAttribute("resultMsg", "회원 탈퇴 성공");
    		return true;
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "회원 탈퇴 실패.");
    		return false;
		}
    }
    
	public Optional<Owner> findOwnerByUserUuid(String uuid) {
		UUID uuidObj = UUID.fromString(uuid);
		Optional<User> user = userRepository.findByUserUuid(uuidObj);
		System.out.println("🧩 User 존재 여부: " + (user.isPresent() ? "있음" : "없음"));

		return user.flatMap(u -> {
			System.out.println("🔎 UserId로 Owner 찾기: " + u.getUserId());
			return ownerRepository.findByUser_UserId(u.getUserId());
		});
	}

}

