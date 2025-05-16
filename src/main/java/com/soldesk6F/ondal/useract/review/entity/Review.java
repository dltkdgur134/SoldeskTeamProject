package com.soldesk6F.ondal.useract.review.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.order.entity.Order;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "review")
public class Review {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "review_id" , nullable = false, unique = true)
	private UUID reviewId;
	
	@ManyToOne
	@JoinColumn(name = "user_uuid",nullable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;
	
	@OneToOne
	@JoinColumn(name = "order_id" , nullable = false,unique = true)
	private Order order;
	
	@Column(name = "rating", nullable = false, 
	columnDefinition = "DECIMAL(2,1) CHECK (rating >= 1.0 AND rating <= 5.0)")
	@DecimalMin(value = "1.0")
	@DecimalMax(value = "5.0")
	private double rating;
	
	@Column(name = "review_title" , nullable = false,length = 30)
	private String reviewTitle;
	
	@Lob
	@Column(name = "review_content" , nullable = false)
	private String reviewContent;
	
	@CreationTimestamp
	@Column(name = "created_date" , nullable = false ,updatable = false)
	private LocalDateTime createdDate;
	
	@UpdateTimestamp
	@Column(name = "updated_date", nullable = false)
	private LocalDateTime updatedDate;

	
	@OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private ReviewReply reviewReply;
	
	@Builder
	public Review(User user, Store store, Order order, double rating, String reviewTitle, String reviewContent) {
		super();
		this.user = user;
		this.store = store;
		this.order = order;
		this.rating = rating;
		this.reviewTitle = reviewTitle;
		this.reviewContent = reviewContent;
	}
	public String getReviewUuidAsString() {
	    return reviewId != null ? reviewId .toString() : null;
	}
	
	public void updateReview(double rating, String reviewTitle, String reviewContent) {
		this.rating = rating;
		this.reviewTitle = reviewTitle;
		this.reviewContent= reviewContent;
	}
	
}
