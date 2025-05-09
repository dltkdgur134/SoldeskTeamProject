package com.soldesk6F.ondal.useract.review.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
import com.soldesk6F.ondal.useract.order.repository.OrderDetailRepository;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;
import com.soldesk6F.ondal.useract.review.DTO.ReviewDTO;
import com.soldesk6F.ondal.useract.review.entity.Review;
import com.soldesk6F.ondal.useract.review.entity.ReviewImg;
import com.soldesk6F.ondal.useract.review.repository.ReviewImgRepository;
import com.soldesk6F.ondal.useract.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewImgRepository reviewImgRepository;
	
	@Value("${upload.review.path}")
	private String uploadReviewDir;
	
	// 리뷰 작성할 주문 내역 반환
	@Transactional(readOnly = true)
	public void getOrderForReview(CustomUserDetails userDetails,
			UUID orderId,
			RedirectAttributes redirectAttributes,
			Model model) {
		try {
			String userUUIDString = userDetails.getUser().getUserUuidAsString();
			UUID userUuid = UUID.fromString(userUUIDString);
			Optional<User> findUser = userRepository.findById(userUuid);
			
			if (findUser.isEmpty()) {
				redirectAttributes.addFlashAttribute("result", 1);
				redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
			}
			String orderUUIDString = orderId.toString();
			UUID orderUuid = UUID.fromString(orderUUIDString);
			Optional<Order> findOrder = orderRepository.findById(orderUuid);
			
			if (findOrder.isEmpty()) {
				redirectAttributes.addFlashAttribute("result", 1);
				redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 주문 내역입니다.");
			} else {
				Order order = findOrder.get();
				
				List<OrderDetail> orderDetail = orderDetailRepository.findByOrder(order);
				model.addAttribute("order", order);
				model.addAttribute("orderDetail", orderDetail);
			}
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "서버 통신 에러.");
		}
	}
	
	// 유저가 작성한 모든 리뷰 표기
	@Transactional(readOnly = true)
	public Optional<List<Review>> getAllReviews(CustomUserDetails userDetails,
			RedirectAttributes redirectAttributes,
			Model model) {
		String userUUIDString = userDetails.getUser().getUserUuidAsString();
		UUID userUuid = UUID.fromString(userUUIDString);
		Optional<User> findUser = userRepository.findById(userUuid);
		
		if (findUser.isEmpty()) {
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
			return null;
		}
		User user = findUser.get();
		
		Optional<List<Review>> findReviewList = reviewRepository.findAllByUser(user);
		
		if (findReviewList.isEmpty() || findReviewList.get().isEmpty()) {
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "작성한 리뷰가 없습니다..");
			return null;
		}
		// 맵에 이미지 파일들 (다수) 넣기 -> 키 = 리뷰 / 값 = 리뷰이미지 리스트
		List<Review> reviewList = findReviewList.get();
		Map<UUID, List<ReviewImg>> reviewImgMap = new HashMap<>();
		
		for (Review review : reviewList) {
			Optional<List<ReviewImg>> findReviewImgList = reviewImgRepository.findAllByReview(review);
			reviewImgMap.put(review.getReviewId(), findReviewImgList.orElse(Collections.emptyList()));
		}
		
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("reviewImgMap", reviewImgMap);
		return findReviewList;
	}
	
	
	// 리뷰 작성
	@Transactional
	public boolean registerReview(CustomUserDetails userDetails,
			ReviewDTO reviewDTO,
			RedirectAttributes redirectAttributes) {
		try {
			String userUUIDString = userDetails.getUser().getUserUuidAsString();
			UUID userUuid = UUID.fromString(userUUIDString);
			Optional<User> findUser = userRepository.findById(userUuid);
			
			if (findUser.isEmpty()) {
				redirectAttributes.addFlashAttribute("result", 1);
    			redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
    			return false;
			}
			User user = findUser.get();
			
			String orderUUIDString = reviewDTO.getOrderUuidAsString();
			UUID orderId = UUID.fromString(orderUUIDString);
			
			Optional<Order> findOrder = orderRepository.findById(orderId);
			
			if (findOrder.isEmpty()) {
				redirectAttributes.addFlashAttribute("result", 1);
    			redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 주문입니다.");
    			return false;
			}
			Order order = findOrder.get();
			
			Store store = order.getStore();
			
			if (store == null) {
				redirectAttributes.addFlashAttribute("result", 1);
    			redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 가게입니다.");
    			return false;
			} 
			double rating = Double.parseDouble(reviewDTO.getRating());
			
			String reviewTitle = reviewDTO.getReviewTitle().trim();
			String reviewContent = reviewDTO.getReviewContent();
			
			Review review = Review.builder()
					.user(user)
					.store(store)
					.order(order)
					.rating(rating)
					.reviewTitle(reviewTitle)
					.reviewContent(reviewContent)
					.build();
			
			reviewRepository.save(review);
			
			redirectAttributes.addFlashAttribute("result", 0);
			redirectAttributes.addFlashAttribute("resultMsg", "리뷰 등록 성공!");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// 리뷰 이미지 업로드
	@Transactional
	public boolean uploadReviewImage(CustomUserDetails userDetails,
			ReviewDTO reviewDTO,
			MultipartFile[] reviewImage,
			RedirectAttributes redirectAttributes) {
		try {
			String userUUIDString = userDetails.getUser().getUserUuidAsString();
			UUID userUuid = UUID.fromString(userUUIDString);
			Optional<User> findUser = userRepository.findById(userUuid);
			
			if (findUser.isEmpty()) {
				redirectAttributes.addFlashAttribute("result", 1);
				redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
				return false;
			}
			//User user = findUser.get();
			
			if (reviewImage != null) {
				int count = 1;
				for (MultipartFile image : reviewImage) {
					String orderUUIDString = reviewDTO.getOrderUuidAsString();
					UUID orderId = UUID.fromString(orderUUIDString);
					
					Optional<Order> findOrder = orderRepository.findById(orderId);
					
					if (findOrder.isEmpty()) {
						redirectAttributes.addFlashAttribute("result", 1);
						redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 주문입니다.");
						return false;
					}
					Order order = findOrder.get();
					
					Optional<Review> findReview = reviewRepository.findByOrder(order);
					// 리뷰가 존재하는지 확인
					if (findReview.isEmpty()) {
						redirectAttributes.addFlashAttribute("result", 1);
						redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 리뷰입니다.");
						return false;
					}
					Review registeredReview = findReview.get();
					
					String reviewImgFileName = image.getOriginalFilename();
					String extension = reviewImgFileName.substring(reviewImgFileName.lastIndexOf(".") + 1);
					String fileName = registeredReview.getReviewUuidAsString() + "_" + count + "." + extension;
					String savePath = new File(uploadReviewDir).getAbsolutePath();
					System.out.println(savePath);
					File saveFolder = new File(savePath);
					if (!saveFolder.exists()) {
						saveFolder.mkdirs();
					}
					// 지정한 upload directory로 파일 이동
					File saveFile = new File(saveFolder, fileName);
					image.transferTo(saveFile);
					
					ReviewImg reviewImg = ReviewImg.builder()
							.review(registeredReview)
							.reviewImg(fileName)
							.build();
					reviewImgRepository.save(reviewImg);
					count ++;
				}
				redirectAttributes.addFlashAttribute("result", 0);
    			redirectAttributes.addFlashAttribute("resultMsg", "리뷰 등록 성공!.");
				return true;
			}
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "리뷰 이미지 업로드 실패.");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "리뷰 이미지 업로드 실패.");
			return false;
		}
	}
	
	@Transactional
	public boolean updateReview(CustomUserDetails userDetails,
			ReviewDTO reviewDTO,
			RedirectAttributes redirectAttributes) {
		try {
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	
	// 리뷰 삭제
	@Transactional
	public boolean deleteReview(CustomUserDetails userDetails,
			UUID reviewId) {
		try {
			String reviewIdString = reviewId.toString();
			UUID reviewUuid = UUID.fromString(reviewIdString);
			Optional<Review> findReview = reviewRepository.findById(reviewUuid);
			
			if (findReview.isEmpty()) {
				return false;
			}
			Review review = findReview.get();
			
			String userUUIDString = userDetails.getUser().getUserUuidAsString();
			UUID userUuid = UUID.fromString(userUUIDString);
			Optional<User> findUser = userRepository.findById(userUuid);
			
			if (findUser.isEmpty()) {
				return false;
			}
			User user = findUser.get();
			
			if (!review.getUser().getUserUuidAsString().equals(user.getUserUuidAsString())) {
				return false;
			}
			Optional<List<ReviewImg>> findReviewImgList = reviewImgRepository.findAllByReview(review);
			List<ReviewImg> reviewImgList = findReviewImgList.get();
			String savePath = new File(uploadReviewDir).getAbsolutePath();
			for (ReviewImg reviewImg : reviewImgList) {
				String fileName = reviewImg.getReviewImg();
				Path reviewImgPath = Paths.get(savePath, fileName);
				if (Files.exists(reviewImgPath)) {
					Files.delete(reviewImgPath);
				}
				reviewImgRepository.delete(reviewImg);
			}
			reviewRepository.delete(review);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
}
