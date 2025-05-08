package com.soldesk6F.ondal.useract.review.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.useract.review.entity.Review;
import com.soldesk6F.ondal.useract.review.entity.ReviewImg;

public interface ReviewImgRepository extends JpaRepository<ReviewImg, UUID>{
	Optional<List<ReviewImg>> findAllByReview(Review review);
	
	
}
