package com.soldesk6F.ondal.user.controller.user;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
import com.soldesk6F.ondal.useract.payment.dto.OndalPayChargeRequest;
import com.soldesk6F.ondal.useract.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UpdateUserController {

	private final UserService userService;
	private final PaymentService paymentService;
	
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
	        redirectAttributes.addFlashAttribute("success", "비밀번호 확인 성공");
	        model.addAttribute("ondalWallet", freshUser.getOndalWallet());
	        model.addAttribute("userUUID", freshUser.getUserUuidAsString());
	        
			return "redirect:/userWallet";
	    }else {
	    	redirectAttributes.addFlashAttribute("error", "비밀번호가 틀렸습니다.");
		}

		return "redirect:/myPage";
	}

	@PostMapping("/checkUserPasswordAndTryOndalPay")
	public String checkUserPasswordAndGoPoint(
			@RequestParam(value = "currentPassword", required = false) String Password,
			@RequestParam(value = "cartUUID") UUID cartUUID,
			@RequestParam(value ="reqDel")String reqDel,
			@RequestParam(value ="reqStore")String reqStore,			
			@AuthenticationPrincipal CustomUserDetails userDetails,
			RedirectAttributes redirectAttributes,Model model
			){
		
		boolean isCorrect = userService.checkPassword(userDetails, Password, redirectAttributes);
		
		if (isCorrect) {
			
			String Paystatus =  paymentService.tryOndalPay(cartUUID,reqDel,reqStore);
			if(Paystatus != null) {
				String resultAndStatus [] = Paystatus.split(":@:");
				if(resultAndStatus[1].equals("성공")) {
					
					return "redirect:/";
				}else {
			        model.addAttribute("cartUUID" , cartUUID);
					model.addAttribute("failReason",resultAndStatus[0]);
					model.addAttribute("status",resultAndStatus[1]);
					return "forward:/store/pay";
				}
			}
			
			
		}
        model.addAttribute("cartUUID" , cartUUID);
        model.addAttribute("status","실패");
        model.addAttribute("failReason","비밀번호가 틀렸습니다");
        return "forward:/store/pay";
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
	        redirectAttributes.addFlashAttribute("success", "비밀번호 확인 성공");
	        model.addAttribute("ondalWallet", freshUser.getOndalWallet());
	        model.addAttribute("ondalPay", freshUser.getOndalPay());
	        model.addAttribute("userSelectedAddress", freshUser.getUserSelectedAddress());
			return "redirect:/ondalPay";
		}else {
			redirectAttributes.addFlashAttribute("error", "비밀번호가 틀렸습니다.");
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
	        redirectAttributes.addFlashAttribute("success", "비밀번호 확인 성공");
	        model.addAttribute("ondalWallet", freshUser.getOndalWallet());
	        model.addAttribute("ondalPay", freshUser.getOndalPay());
	        model.addAttribute("userSelectedAddress", freshUser.getUserSelectedAddress());
			return "redirect:/ondalPay";
	    }else {
	    	redirectAttributes.addFlashAttribute("error", "비밀번호가 틀렸습니다.");
		}
		return "redirect:/userWallet";
	}
	
	
	
	@PostMapping("/user/buyOndalPay")
	public String buyOndalPay(
			@RequestParam(value = "amount", required = false) Integer amount,
	        @RequestParam("Password") String password,
	        @RequestParam("toss_order_id") String tossOrderId,
	        @AuthenticationPrincipal CustomUserDetails userDetails,
	        RedirectAttributes redirectAttributes) {

	    boolean isCorrect = userService.checkPassword(userDetails, password, redirectAttributes);
	    if (!isCorrect) {
	        redirectAttributes.addFlashAttribute("error", "비밀번호가 틀렸습니다.");
	        return "redirect:/ondalPay";
	    }
	    if (amount == null) {
            redirectAttributes.addFlashAttribute("error", "충전 금액을 입력해주세요.");
            return "redirect:/ondalPay";
        }


	    try {
	        OndalPayChargeRequest chargeRequest = new OndalPayChargeRequest();
	        chargeRequest.setAmount(amount);
	        chargeRequest.setTossOrderId(tossOrderId);
	        // 기본값 paymentMethod, paymentUsageType, paymentStatus는 DTO 내부에서 설정됨

	        
	        // 컨트롤러에서 LocalDateTime으로 변환해서 서비스에 전달
	        userService.chargeOndalWallet(chargeRequest, userDetails.getUser().getUserUuid());

	        redirectAttributes.addFlashAttribute("success", amount + "원이 O Pay로 충전되었습니다.");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "충전 중 오류가 발생했습니다: " + e.getMessage());
	    }

	    return "redirect:/ondalPay";
	}
}

