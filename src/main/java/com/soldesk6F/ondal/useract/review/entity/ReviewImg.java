package com.soldesk6F.ondal.useract.review.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "review_img",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"review_id", "review_img"})
    })
public class ReviewImg {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "review_img_id" , nullable = false, unique = true)
	private UUID reviewImgId;
	
	@ManyToOne
	@JoinColumn(name = "review_id",nullable = false)
	private Review review;
	
	@Column(name = "review_img", nullable = false , length = 255)
	private String reviewImg;

	@Builder
	public ReviewImg(Review review, String reviewImg) {
		super();
		this.review = review;
		this.reviewImg = reviewImg;
	}
	public String getReviewImgUuidAsString() {
	    return reviewImgId != null ? reviewImgId .toString() : null;
	}
	
	
}
