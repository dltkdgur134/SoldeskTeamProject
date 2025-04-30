package com.soldesk6F.ondal.useract.review.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.useract.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReviewController {
	
	private final ReviewService reviewService;
	
	@GetMapping("/writeReview/{orderId}")
	public String goWriteReview(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("orderId") UUID orderId,
			RedirectAttributes redirectAttributes,
			Model model) {
		reviewService.getOrderForReview(userDetails, orderId, redirectAttributes, model);
		return "/content/writeReview";
	}
	
	@PostMapping("/content/regReview")
	public String regReview() {
		
		return "/content/index";
	}
	
	
	
}
