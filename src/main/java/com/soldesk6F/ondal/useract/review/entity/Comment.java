package com.soldesk6F.ondal.useract.review.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "comment")
public class Comment {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "comment_id" , nullable = false , unique = true)
	private UUID commentId;
	
	@OneToMany
	
	
	
	@OneToOne
	@JoinColumn(name = "review_id" ,nullable = false)
	private Review review;
	
	@Column(name = "comment_content",nullable = false , length = 255)
	private String commentContent;
	
	
}
