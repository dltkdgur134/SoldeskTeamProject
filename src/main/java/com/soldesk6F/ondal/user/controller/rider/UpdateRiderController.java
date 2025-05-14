package com.soldesk6F.ondal.user.controller.rider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.Rider.DeliveryRange;
import com.soldesk6F.ondal.user.repository.RiderRepository;
import com.soldesk6F.ondal.user.service.RiderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UpdateRiderController {
	private final RiderRepository riderRepository;
	private final RiderService riderService;

	@Autowired
	private PasswordEncoder passwordEncoder; // 필드 주입

	@GetMapping("rider/riderMypage")
	public String showEditPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		String userId = userDetails.getUser().getUserId();
		Rider rider = riderService.getRiderByUserId(userId); // rider 정보 불러오기
		model.addAttribute("rider", rider);
		return "content/rider/riderMypage";
	}

	@GetMapping("rider/updateRiderInfo")
	public String showUpdateRiderInfoForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		String userId = userDetails.getUser().getUserId();
		Rider rider = riderService.getRiderByUserId(userId); // rider 정보 불러오기
		model.addAttribute("rider", rider);
		return "content/rider/riderUpdateInfo"; // 수정 폼 페이지
	}

	@PostMapping("/updateRiderNickname")
	public String updateRiderNickname(@RequestParam("riderNickname") String riderNickname,
			@AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes redirectAttributes) {

		String userId = userDetails.getUser().getUserId();

		try {
			// 닉네임만 업데이트 (다른 필드는 null 또는 기본값)
			riderService.updateRiderInfo(userId, riderNickname, // 닉네임
					null, // vehicleNumber
					null, // riderPhone
					null, // riderHubAddress
					0.0, // hubAddressLatitude
					0.0, // hubAddressLongitude
					null // deliveryRange
			);
			redirectAttributes.addFlashAttribute("InfoUpdateSuccess", "닉네임이 정상적으로 변경되었습니다.");
		} catch (DataIntegrityViolationException ex) {
			// 길이 초과, null 위반, unique 제약 등
			redirectAttributes.addFlashAttribute("InfoUpdateError", "정보 변경이 실패했습니다.");
		} catch (Exception ex) {
			// 그 외 예외
			redirectAttributes.addFlashAttribute("InfoUpdateError", "정보 변경이 실패했습니다.");
		}

		return "redirect:/rider/riderInfopage";
	}

	@PostMapping("/updateRiderInfo")
	public String updateRiderInfo(
			@RequestParam(value = "currentSecondaryPassword", required = false) String currentSecondaryPassword,
			@RequestParam(value = "newSecondaryPassword", required = false) String newSecondaryPassword,
			@RequestParam(value = "confirmNewSecondaryPassword", required = false) String confirmNewSecondaryPassword,
			@RequestParam("vehicleNumber") String vehicleNumber, @RequestParam("riderPhone") String riderPhone,
			@RequestParam("riderHubAddress") String riderHubAddress,
			@RequestParam("hubAddressLatitude") double hubAddressLatitude,
			@RequestParam("hubAddressLongitude") double hubAddressLongitude,
			@RequestParam("deliveryRange") String deliveryRange, @AuthenticationPrincipal CustomUserDetails userDetails,
			RedirectAttributes redirectAttributes) {

		String userId = userDetails.getUser().getUserId();
		Rider rider = riderRepository.findByUser_UserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("라이더 정보가 존재하지 않습니다."));

		boolean wantChangePw = StringUtils.hasText(newSecondaryPassword)
				|| StringUtils.hasText(confirmNewSecondaryPassword);

		// 1) 2차 비밀번호 변경을 시도했으면, 미리 검증하고 실패 시 리턴
		if (wantChangePw) {
			if (!newSecondaryPassword.equals(confirmNewSecondaryPassword)) {
				redirectAttributes.addFlashAttribute("InfoUpdateError", "새로운 2차 비밀번호와 확인이 일치하지 않습니다.");
				return "redirect:/rider/riderInfopage";
			}
			if (!StringUtils.hasText(currentSecondaryPassword)
					|| !passwordEncoder.matches(currentSecondaryPassword, rider.getSecondaryPassword())) {
				redirectAttributes.addFlashAttribute("InfoUpdateError", "현재 2차 비밀번호가 일치하지 않습니다.");
				return "redirect:/rider/riderInfopage";
			}
		}

		try {
			// 2) (선택) 2차 비밀번호 업데이트
			if (wantChangePw) {
				riderService.updateRiderSecondaryPassword(rider, currentSecondaryPassword, newSecondaryPassword);
				redirectAttributes.addFlashAttribute("InfoUpdateSuccess", "2차 비밀번호가 정상적으로 변경되었습니다.");
			}

			// 3) 라이더 정보 업데이트
			DeliveryRange rangeEnum = DeliveryRange.valueOf(deliveryRange);
			riderService.updateRiderInfo(userId, null, vehicleNumber, riderPhone, riderHubAddress, hubAddressLatitude,
					hubAddressLongitude, rangeEnum);

			// 4) 비밀번호 변경 없이 정보만 수정했으면 성공 메시지
			if (!wantChangePw) {
				redirectAttributes.addFlashAttribute("InfoUpdateSuccess", "정보가 정상적으로 변경되었습니다.");
			}

		} catch (DataIntegrityViolationException ex) {
			// DB 제약조건 위반 (길이, null, unique 등)
			redirectAttributes.addFlashAttribute("InfoUpdateError", "정보 변경이 실패했습니다.");
		} catch (Exception ex) {
			// 기타 예외
			redirectAttributes.addFlashAttribute("InfoUpdateError", "정보 변경이 실패했습니다.");
		}

		return "redirect:/rider/riderInfopage";
	}

	@GetMapping("rider/riderWallet")
	public String showRiderWallet(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		String userId = userDetails.getUser().getUserId();
		Rider rider = riderService.getRiderByUserId(userId); // rider 정보 불러오기
		model.addAttribute("rider", rider);
		return "content/rider/riderWallet";
	}

	@PostMapping("/checkRiderSecondaryPassword")
	public String checkRiderSecondaryPassword(
			@RequestParam(value = "currentSecondaryPassword", required = false) String currentSecondaryPassword,
			@AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes redirectAttributes) {
		String userId = userDetails.getUser().getUserId();
		Rider rider = riderRepository.findByUser_UserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("라이더 정보가 존재하지 않습니다."));

		if (rider.isSecondaryPasswordLocked()) {
			redirectAttributes.addFlashAttribute("SecondaryPasswordError",
					"2차 비밀번호가 5회 이상 틀려 입력이 잠겼습니다. 10분 후 다시 시도하세요.");
			return "redirect:/rider/riderInfopage";
		}
		boolean isCorrect = riderService.checkRiderSecondaryPassword(rider, currentSecondaryPassword);

		if (isCorrect) {
			return "redirect:/rider/riderWallet";
		}
		// 실패한 경우 현재 실패 횟수 가져와서 메시지 구성
		int failCount = rider.getSecondaryPasswordFailCount();
		int remaining = 5 - failCount;
		redirectAttributes.addFlashAttribute("SecondaryPasswordError",
				"잘못된 2차 비밀번호입니다. (" + failCount + "회 실패, " + remaining + "회 남음)");

		return "redirect:/rider/riderInfopage"; // 2차 비밀번호 확인 실패 시 라이더 정보 페이지로
	}

	@PostMapping("/rider/withdraw")
	public String withdraw(@RequestParam("amount") int amount,
			@RequestParam("secondaryPassword") String secondaryPassword,
			@AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes redirectAttributes) {

		String userId = userDetails.getUser().getUserId();
		Rider rider = riderService.getRiderByUserId(userId); // 라이더 정보 가져오기

		// 출금 처리 서비스 호출
		String resultMessage = riderService.processWithdrawal(rider, amount, secondaryPassword);

		if (resultMessage.startsWith("출금 성공")) {
			redirectAttributes.addFlashAttribute("success", resultMessage);
		} else {
			redirectAttributes.addFlashAttribute("error", resultMessage);
		}

		return "redirect:/rider/riderWallet";
	}

	@PostMapping("/rider/convertToOndal")
	public String convertRiderToOndal(@RequestParam("amount") int amount,
			@RequestParam("secondaryPassword") String secondaryPassword,
			@AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes redirectAttributes) {
		String userId = userDetails.getUser().getUserId();
		Rider rider = riderService.getRiderByUserId(userId); // 라이더 정보 가져오기
		
		String resultMessage = riderService.convertRiderWalletToOndalWallet(rider, amount,
				secondaryPassword);

		if (resultMessage.startsWith("전환 성공")) {
			redirectAttributes.addFlashAttribute("success", resultMessage);
		} else {
			redirectAttributes.addFlashAttribute("error", resultMessage);
		}

		return "redirect:/rider/riderWallet";
	}

}
