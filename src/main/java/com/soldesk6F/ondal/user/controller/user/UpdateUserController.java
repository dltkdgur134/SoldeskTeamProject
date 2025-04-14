package com.soldesk6F.ondal.user.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.soldesk6F.ondal.OndalApplication;
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UpdateUserController {

    private final UserRepository userRepository;
	
	private final UserService userService;

	@PostMapping("/checkNickname") 
	@ResponseBody
	public Map<Object, Object> checkNickname(@RequestParam("nickname") String nickName,
			Model model,
			RedirectAttributes rAttr) {
		
		Map<Object, Object> map = new HashMap<>();
		
		if (userRepository.existsByNickName(nickName) == true) {
			map.put("count", 1);
		} else {
			map.put("count", 0);
		}
		return map;
	}
	
	
	@PostMapping("/content/updateNickname")
	public String updateNickname(
			@RequestParam("nickname") String nickName, 
			Model model,
			RedirectAttributes rAttr) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = customUserDetails.getUser();
		
		if (!userService.updateUserNickname(nickName, user, model)) {
			rAttr.addFlashAttribute("result", "닉네임을 변경할 수 없습니다.");
			return "redirect:/infopage";
		}
		customUserDetails.getUser().setNickName(nickName);
	    rAttr.addFlashAttribute("result", "닉네임 변경 성공!");
		return "redirect:/infopage";
	}
	
	@PostMapping("/content/updateProfilePic")
	public String updateProfilePic(
			@RequestParam("profileImage") MultipartFile profileImage, Model model,
			RedirectAttributes rAttr) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = customUserDetails.getUser();
		
		if (!userService.updateUserPicture(user, profileImage, model)) {
			rAttr.addFlashAttribute("result", "프로필 이미지 변경 실패!");
			return "redirect:/infopage";
		}
		customUserDetails.getUser().setUserProfile(userRepository.findByUserId(user.getUserId()).get().getUserProfile());
		rAttr.addFlashAttribute("result", "프로필 이미지 변경 성공!");
		return "redirect:/infopage";
	}
	
	@PostMapping("/checkPhoneNum")
	@ResponseBody
	public Map<Object, Object> checkPhoneNum(@RequestParam("userPhone") String userPhone,
			RedirectAttributes rattr) {
		
		Map<Object, Object> map = new HashMap<>();
		
		if (userRepository.existsByUserPhone(userPhone)) {
			map.put("count", 1);
		} else {
			map.put("count", 0);
		}
		return map;
	}
	
	@PostMapping("/content/updatePhoneNum")
	public String updatePhoneNum(
			@RequestParam("userPhone") String userPhone,
			RedirectAttributes rAttr,
			Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = customUserDetails.getUser();
		
		if (!userService.updateUserPhone(userPhone, user, model)) {
			rAttr.addFlashAttribute("result", "전화번호 변경 실패!");
			return "redirect:/infopage";
		}
		customUserDetails.getUser().setUserPhone(userRepository.findByUserId(user.getUserId()).get().getUserPhone());
		rAttr.addFlashAttribute("result", "전화번호 변경 성공!");
		return "redirect:/infopage";
	}
	
	
}

