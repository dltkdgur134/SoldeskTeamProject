package com.soldesk6F.ondal.useract.review.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.store.entity.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "review_reply")
public class ReviewReply {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "review_reply_id" , nullable = false , unique = true)
	private UUID reviewReplyId;
	
	@OneToOne
	@JoinColumn(name = "review_id" ,nullable = false)
	private Review review;
	
	@ManyToOne
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;
	
	@Column(name = "review_reply_content",nullable = false , length = 255)
	private String reviewReplyContent;
	
	@CreationTimestamp
	@Column(name = "created_date" , nullable = false , updatable = false)
	private LocalDateTime createdDate;
	
	@UpdateTimestamp
	@Column(name = "updated_date" , nullable = false)
	private LocalDateTime updatedDate;

	@Builder
	public ReviewReply(Review review, Store store, String reviewReplyContent) {
		super();
		this.review = review;
		this.store = store;
		this.reviewReplyContent = reviewReplyContent;
	}

	public String getReviewReplyUuidAsString() {
	    return reviewReplyId != null ? reviewReplyId .toString() : null;
	}
	
	
}
