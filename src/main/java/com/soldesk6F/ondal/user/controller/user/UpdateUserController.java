package com.soldesk6F.ondal.user.controller.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UpdateUserController {

	private final UserService userService;
	
	// 닉네임 중복확인
	@PostMapping("/checkNickname") 
	@ResponseBody
	public Map<Object, Object> checkNickname(
			@RequestParam("nickname") String nickName,
			RedirectAttributes redirectAttributes) {
		Map<Object, Object> response = new HashMap<>();
		boolean nickNameExists = userService.isNicknameDuplicate(nickName);
		response.put("count", nickNameExists ? 1 : 0); // 닉네임이 있으면 1 (중복확인 통과 X) 없으면 0 (통과)
		return response;
	}
	
	// 닉네임 수정
	@PostMapping("/content/updateNickname")
	public String updateNickname(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("nickname") String nickName,
			RedirectAttributes redirectAttributes) {
		userService.updateUserNickname(userDetails, nickName, redirectAttributes); 
		return "redirect:/myPage";
	}
	
	// 유저 프로필 사진 수정
	@PostMapping("/content/updateProfilePic")
	public String updateProfilePic(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("profileImage") MultipartFile profileImage,
			RedirectAttributes redirectAttributes) {
		userService.updateUserPicture(userDetails, profileImage, redirectAttributes);
		return "redirect:/myPage";
	}
	
	// 전화번호 중복 확인
	@PostMapping("/checkPhoneNum")
	@ResponseBody
	public Map<Object, Object> checkPhoneNum(
			@RequestParam("userPhone") String userPhone,
			RedirectAttributes redirectAttributes) {
		Map<Object, Object> response = new HashMap<>();
		boolean userPhoneExists = userService.isPhoneDuplicate(userPhone);
		response.put("count", userPhoneExists ? 1 : 0); // 전화번호가 있으면 1 (중복확인 통과 X) 없으면 0 (통과)
		return response;
	}
	
	// 전화번호 수정
	@PostMapping("/content/updatePhoneNum")
	public String updatePhoneNum(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("userPhone") String userPhone,
			RedirectAttributes redirectAttributes) {
		userService.updateUserPhone(userDetails, userPhone, redirectAttributes); 
		return "redirect:/myPage";
	}
	
	// 비밀번호 수정
	@PostMapping("/content/updatePassword")
	public String updatePassword(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("oldPassword") String oldPassword,
			@RequestParam("password") String password,
			RedirectAttributes redirectAttributes) {
		if (!userService.updatePassword(userDetails, oldPassword, password, redirectAttributes)) {
			return "redirect:/mySecurity";
		}
		return "redirect:/logout";
	}
	
	private final UserRepository userRepository;
	@PostMapping("/checkUserPasswordAndGoWallet")
	public String checkUserPasswordAndGoWallet(
			@RequestParam(value = "currentPassword", required = false) String currentPassword,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			RedirectAttributes redirectAttributes, Model model
			){
		
		boolean isCorrect = userService.checkPassword(userDetails, currentPassword, redirectAttributes);

		if (isCorrect) {
			UUID userUuid = UUID.fromString(userDetails.getUser().getUserUuidAsString());
	        User freshUser = userRepository.findById(userUuid)
	                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
	        
	        model.addAttribute("ondalWallet", freshUser.getOndalWallet());
	        model.addAttribute("userSelectedAddress", freshUser.getUserSelectedAddress());
	        
			return "/content/user/userWallet";
	    }

		return "redirect:/myPage";
	}
	@PostMapping("/checkUserPasswordAndGoOndalPay")
	public String checkUserPasswordAndGoPoint(
			@RequestParam(value = "Password", required = false) String Password,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			RedirectAttributes redirectAttributes,Model model
			){
		
		boolean isCorrect = userService.checkPassword(userDetails, Password, redirectAttributes);
		
		if (isCorrect) {
			UUID userUuid = UUID.fromString(userDetails.getUser().getUserUuidAsString());
	        User freshUser = userRepository.findById(userUuid)
	                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
	        
	        model.addAttribute("ondalWallet", freshUser.getOndalWallet());
	        model.addAttribute("ondalPay", freshUser.getOndalPay());
	        model.addAttribute("userSelectedAddress", freshUser.getUserSelectedAddress());
			return "/content/user/ondalPay";
		}
		
		return "redirect:/myPage";
	}
	@PostMapping("/user/goToPoints")
	public String withdraw(
	        @RequestParam("Password") String Password,
	        @AuthenticationPrincipal CustomUserDetails userDetails,
	        RedirectAttributes redirectAttributes,Model model) {
		boolean isCorrect = userService.checkPassword(userDetails, Password, redirectAttributes);
		
		if (isCorrect) {
			UUID userUuid = UUID.fromString(userDetails.getUser().getUserUuidAsString());
	        User freshUser = userRepository.findById(userUuid)
	                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
	        
	        model.addAttribute("ondalWallet", freshUser.getOndalWallet());
	        model.addAttribute("ondalPay", freshUser.getOndalPay());
	        model.addAttribute("userSelectedAddress", freshUser.getUserSelectedAddress());
			return "/content/user/ondalPay";
	    }
		return "/content/user/userWallet";
	}
	
	
	
	@PostMapping("/user/buyOndalPay")
	public String buyOndalPay(@RequestParam("amount") int amount,
			@RequestParam("Password") String Password,
	                          @AuthenticationPrincipal CustomUserDetails userDetails,
	                          RedirectAttributes redirectAttributes) {
		boolean isCorrect = userService.checkPassword(userDetails, Password, redirectAttributes);
		if(isCorrect) {
			try {
				userService.convertOndalWalletToPay(userDetails, amount);
				redirectAttributes.addFlashAttribute("success", amount + "원이 O Pay로 충전되었습니다.");
			} catch (IllegalArgumentException e) {
				redirectAttributes.addFlashAttribute("error", e.getMessage());
			}
		}

	    return "redirect:/ondalPay"; // 충전 결과를 보여줄 페이지로 이동
	}
}

