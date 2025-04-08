package com.soldesk6F.ondal.useract.searchHistory.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "search_history")
public class SearchHistory {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "search_history_id" , nullable = false , unique = true)
	private UUID searchHistoryId;
	
	@ManyToOne
	@JoinColumn(name = "user_id",nullable = false)
	private User user;
	
	@Column(name = "search_name" , nullable = false ,length = 30)
	private String searchName;
	
	@CreationTimestamp
	@Column(name = "created_date", nullable = false , updatable = false)
	private LocalDateTime createdDate;

	@Builder
	public SearchHistory(User user, String searchName) {
		super();
		this.user = user;
		this.searchName = searchName;
	}
	
	
	
}
