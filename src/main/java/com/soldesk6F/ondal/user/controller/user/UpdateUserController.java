package com.soldesk6F.ondal.user.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UpdateUserController {

	private final UserService userService;
	
	@PostMapping("/checkNickname") 
	@ResponseBody
	public Map<Object, Object> checkNickname(
			@RequestParam("nickname") String nickName,
			RedirectAttributes rAttr) {
		Map<Object, Object> response = new HashMap<>();
		boolean nickNameExists = userService.isNicknameDuplicate(nickName);
		response.put("count", nickNameExists ? 1 : 0); // 닉네임이 있으면 1 (중복확인 통과 X) 없으면 0 (통과)
		return response;
	}
	
	@PostMapping("/content/updateNickname")
	public String updateNickname(
			@AuthenticationPrincipal CustomUserDetails cud,
			@RequestParam("nickname") String nickName,
			RedirectAttributes rAttr) {
//		String userId = cud.getUser().getUserId(); business logic 컨트롤러에 노출
		userService.updateUserNickname(cud, nickName, rAttr);
		return "redirect:/myPage";
	}
	
	@PostMapping("/content/updateProfilePic")
	public String updateProfilePic(
			@AuthenticationPrincipal CustomUserDetails cud,
			@RequestParam("profileImage") MultipartFile profileImage,
			RedirectAttributes rAttr) {
		userService.updateUserPicture(cud, profileImage, rAttr);
		return "redirect:/myPage";
	}
	
	@PostMapping("/checkPhoneNum")
	@ResponseBody
	public Map<Object, Object> checkPhoneNum(
			@RequestParam("userPhone") String userPhone,
			RedirectAttributes rattr) {
		Map<Object, Object> response = new HashMap<>();
		boolean userPhoneExists = userService.isPhoneDuplicate(userPhone);
		response.put("count", userPhoneExists ? 1 : 0); // 전화번호가 있으면 1 (중복확인 통과 X) 없으면 0 (통과)
		return response;
	}
	
	@PostMapping("/content/updatePhoneNum")
	public String updatePhoneNum(
			@AuthenticationPrincipal CustomUserDetails cud,
			@RequestParam("userPhone") String userPhone,
			RedirectAttributes rAttr) {
		userService.updateUserPhone(cud, userPhone, rAttr); 
		return "redirect:/myPage";
	}
	
	@PostMapping("/content/passwordCheck")
	public String checkPassword(
			@AuthenticationPrincipal CustomUserDetails cud,
			@RequestParam("oldPassword") String oldPassword,
			@RequestParam("password") String password,
			RedirectAttributes rAttr) {
		if (!userService.updatePassword(cud, oldPassword, password, rAttr)) {
			return "redirect:/mySecurity";
		}
		return "redirect:/myPage";
	}
}

