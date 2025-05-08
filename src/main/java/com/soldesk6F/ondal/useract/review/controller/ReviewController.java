package com.soldesk6F.ondal.useract.review.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.useract.review.DTO.ReviewDTO;
import com.soldesk6F.ondal.useract.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReviewController {
	
	private final ReviewService reviewService;
	
	// 내 리뷰 관리 페이지 이동
	@GetMapping("/myReview")
	public String goMyReview(@AuthenticationPrincipal CustomUserDetails userDetails,
			RedirectAttributes redirectAttributes,
			Model model) {
		reviewService.getAllReviews(userDetails, redirectAttributes, model);
		return "content/myReview";
	}
	
	// 리뷰 작성페이지 이돟
	@GetMapping("/writeReview/{orderId}")
	public String goWriteReview(@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("orderId") UUID orderId,
			RedirectAttributes redirectAttributes,
			Model model) {
		reviewService.getOrderForReview(userDetails, orderId, redirectAttributes, model);
		return "/content/writeReview";
	}
	
	// 리뷰 정보 수정 페이지 이동 (선택한 주소만)
	@GetMapping("/updateReview/{reviewId}")
	public String goUpdateReview(@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("reviewId") UUID reviewId,
			RedirectAttributes redirectAttributes,
			Model model) {
		reviewService.getReview(userDetails, reviewId, redirectAttributes, model);
		return "/content/updateReview";
	}
	
	// 리뷰 작성 (성공 시 주문 내역 이동 / 실패 시 홈페이지 이동)
	@PostMapping("/content/regReview")
	public String regReview(@AuthenticationPrincipal CustomUserDetails userDetails,
			ReviewDTO reviewDTO,
			@RequestParam("reviewImg") MultipartFile[] reviewImg,
			RedirectAttributes redirectAttributes) {
		if (reviewService.registerReview(userDetails, reviewDTO, redirectAttributes)) {
			reviewService.uploadReviewImage(userDetails, reviewDTO, reviewImg, redirectAttributes);
			return "redirect:/orderHistory";
		} else {
			return "redirect:/";
		}
	}
	
	// 리뷰 수정
	@PutMapping("content/updateReview/")
	public String updateReview(@AuthenticationPrincipal CustomUserDetails userDetails,
			ReviewDTO reviewDTO,
			@RequestParam("reviewImg") MultipartFile[] reviewImg,
			RedirectAttributes redirectAttributes) {
		
		
		return "";
	}
	
	
	
	// 리뷰 삭제
	@DeleteMapping("/content/deleteReview/{reviewId}")
	public ResponseEntity<Map<String, Object>> deleteReview(@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("reviewId") UUID reviewId) {
		boolean result = reviewService.deleteReview(userDetails, reviewId);
		Map<String, Object> response = new HashMap<>();
		if (result) {
			response.put("result", 0);
			response.put("resultMsg", "리뷰가 삭제되었습니다.");
			return ResponseEntity.ok(response);
		} else {
			response.put("result", 1);
			response.put("resultMsg", "리뷰 삭제에 실패했습니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}
	
}
