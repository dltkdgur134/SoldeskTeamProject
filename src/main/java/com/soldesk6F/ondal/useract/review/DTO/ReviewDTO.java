package com.soldesk6F.ondal.useract.review.DTO;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewDTO {
	
	private UUID reviewId;
	private UUID orderId;
	private String rating;
	private String reviewTitle;
	private String reviewContent;
	
	public String getOrderUuidAsString() {
		return orderId != null ? orderId.toString() : null;
	}
	
	public String getReviewUuidAsString() {
		return reviewId != null ? reviewId.toString() : null;
	}
	
}
