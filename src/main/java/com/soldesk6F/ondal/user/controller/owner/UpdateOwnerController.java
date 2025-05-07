package com.soldesk6F.ondal.user.controller.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.repository.OwnerRepository;
import com.soldesk6F.ondal.user.service.OwnerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UpdateOwnerController {
	private final OwnerRepository ownerRepository;
	private final OwnerService ownerService;
	
	@Autowired
	private PasswordEncoder passwordEncoder; // 필드 주입

	@GetMapping("owner/ownerMypage")
    public String showEditPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		String userId = userDetails.getUser().getUserId();
		Owner owner = ownerService.getOwnerByUserId(userId);
		model.addAttribute("owner", owner);
		
		return "content/owner/ownerMypage"; //닉네임 수정 폼 페이지
    }
	
	@GetMapping("owner/updateOwnerInfo")
	public String showUpdateOwnerInfoForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		String userId = userDetails.getUser().getUserId();
		Owner owner = ownerService.getOwnerByUserId(userId);
		model.addAttribute("owner",owner);
		return "content/owner/ownerUpdateInfo"; // 수정 폼 페이지
	}
	
	@PostMapping("/updateOwnerNickname")
	public String updateOwnerNickname(
	        @RequestParam("ownerNickname") String ownerNickname,
	        @AuthenticationPrincipal CustomUserDetails userDetails,
	        RedirectAttributes redirectAttributes) {

	    String userId = userDetails.getUser().getUserId();

	    try {
	        // 닉네임만 업데이트 (다른 필드는 null 또는 기본값)
	        ownerService.updateOwnerInfo(
	            userId,
	            ownerNickname  // 닉네임
	        );
	        redirectAttributes.addFlashAttribute("InfoUpdateSuccess", "닉네임이 정상적으로 변경되었습니다.");
	    } catch (DataIntegrityViolationException ex) {
	        // 길이 초과, null 위반, unique 제약 등
	        redirectAttributes.addFlashAttribute("InfoUpdateError",
	                "정보 변경이 실패했습니다.");
	    } catch (Exception ex) {
	        // 그 외 예외
	        redirectAttributes.addFlashAttribute("InfoUpdateError",
	                "정보 변경이 실패했습니다.");
	    }

	    return "redirect:/owner/ownerInfopage";
	}

	@PostMapping("/updateOwnerInfo")
	public String updateOwnerInfo(
	        @RequestParam(value = "currentSecondaryPassword", required = false) String currentSecondaryPassword,
	        @RequestParam(value = "newSecondaryPassword",     required = false) String newSecondaryPassword,
	        @RequestParam(value = "confirmNewSecondaryPassword", required = false) String confirmNewSecondaryPassword,
	        @AuthenticationPrincipal CustomUserDetails userDetails,
	        RedirectAttributes redirectAttributes) {

	    String userId = userDetails.getUser().getUserId();
	    Owner owner  = ownerRepository.findByUser_UserId(userId)
	            .orElseThrow(() -> new IllegalArgumentException("라이더 정보가 존재하지 않습니다."));

	    boolean wantChangePw = StringUtils.hasText(newSecondaryPassword) || StringUtils.hasText(confirmNewSecondaryPassword);

	    // 1) 2차 비밀번호 변경을 시도했으면, 미리 검증하고 실패 시 리턴
	    if (wantChangePw) {
	        if (!newSecondaryPassword.equals(confirmNewSecondaryPassword)) {
	            redirectAttributes.addFlashAttribute("InfoUpdateError", "새로운 2차 비밀번호와 확인이 일치하지 않습니다.");
	            return "redirect:/owner/ownerInfopage";
	        }
	        if (!StringUtils.hasText(currentSecondaryPassword) ||
	            !passwordEncoder.matches(currentSecondaryPassword, owner.getSecondaryPassword())) {
	            redirectAttributes.addFlashAttribute("InfoUpdateError", "현재 2차 비밀번호가 일치하지 않습니다.");
	            return "redirect:/owner/ownerInfopage";
	        }
	    }

	    try {
	        // 2) (선택) 2차 비밀번호 업데이트
	        if (wantChangePw) {
	            ownerService.updateOwnerSecondaryPassword(owner, currentSecondaryPassword, newSecondaryPassword);
	            redirectAttributes.addFlashAttribute("InfoUpdateSuccess", "2차 비밀번호가 정상적으로 변경되었습니다.");
	        }

	    } catch (DataIntegrityViolationException ex) {
	        // DB 제약조건 위반 (길이, null, unique 등)
	        redirectAttributes.addFlashAttribute("InfoUpdateError",
	                "정보 변경이 실패했습니다.");
	    } catch (Exception ex) {
	        // 기타 예외
	        redirectAttributes.addFlashAttribute("InfoUpdateError",
	                "정보 변경이 실패했습니다.");
	    }

	    return "redirect:/owner/ownerInfopage";
	}
	
	@GetMapping("owner/ownerWallet")
    public String showOwnerWallet(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		String userId = userDetails.getUser().getUserId();
		Owner owner = ownerService.getOwnerByUserId(userId); // owner 정보 불러오기
		model.addAttribute("owner", owner);
		return "content/owner/ownerWallet"; 
    }
	
	@PostMapping("/checkOwnerSecondaryPassword")
	public String checkOwnerSecondaryPassword(
			@RequestParam(value = "currentSecondaryPassword", required = false) String currentSecondaryPassword,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			RedirectAttributes redirectAttributes
			){
		String userId = userDetails.getUser().getUserId();
		Owner owner = ownerRepository.findByUser_UserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("점주 정보가 존재하지 않습니다."));
		
		if (owner.isSecondaryPasswordLocked()) {
		    redirectAttributes.addFlashAttribute("SecondaryPasswordError",
		        "2차 비밀번호가 5회 이상 틀려 입력이 잠겼습니다. 10분 후 다시 시도하세요.");
		    return "redirect:/owner/ownerInfopage";
		}
		boolean isCorrect = ownerService.checkOwnerSecondaryPassword(owner, currentSecondaryPassword);

		if (isCorrect) {
	        return "redirect:/owner/ownerWallet";
	    }
		// 실패한 경우 현재 실패 횟수 가져와서 메시지 구성
	    int failCount = owner.getSecondaryPasswordFailCount();
	    int remaining = 5 - failCount;
	    redirectAttributes.addFlashAttribute("SecondaryPasswordError",
	            "잘못된 2차 비밀번호입니다. (" + failCount + "회 실패, " + remaining + "회 남음)");

		return "redirect:/owner/ownerInfopage"; //2차 비밀번호 확인 실패 시 라이더 정보 페이지로
	}
	@PostMapping("/owner/withdraw")
	public String withdraw(
	        @RequestParam("withdrawAmount") int withdrawAmount,
	        @RequestParam("secondaryPassword") String secondaryPassword,
	        @AuthenticationPrincipal CustomUserDetails userDetails,
	        RedirectAttributes redirectAttributes) {

	    String userId = userDetails.getUser().getUserId();
	    Owner owner = ownerService.getOwnerByUserId(userId);  // 라이더 정보 가져오기

	    // 출금 처리 서비스 호출
	    String resultMessage = ownerService.processWithdrawal(owner, withdrawAmount, secondaryPassword);

	    if (resultMessage.startsWith("출금 성공")) {
	        redirectAttributes.addFlashAttribute("withdrawMessage", resultMessage);
	    } else {
	        redirectAttributes.addFlashAttribute("withdrawError", resultMessage);
	    }

	    return "redirect:/owner/ownerWallet";
	}
	
	
	
}
