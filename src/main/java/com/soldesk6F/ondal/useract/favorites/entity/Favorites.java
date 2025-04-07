package com.soldesk6F.ondal.useract.favorites.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.User;

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
@Table(name = "favorites", uniqueConstraints = {
	    @UniqueConstraint(columnNames = {"user_id", "store_id"})
	})	// 한 유저가 같은 가게 즐겨찾기 못하게 막음
public class Favorites {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "favorites_id" , nullable = false, unique = true)
	private UUID favoritesId;
	
	@ManyToOne
	@JoinColumn(name ="user_id" , nullable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;
	
	@Column(name = "description",length = 255)
	private String description;
	
	@CreationTimestamp
	@Column(name = "created_date",nullable = false)
	private LocalDateTime createdDate;

	@Builder
	public Favorites(User user, Store store, String description) {
		super();
		this.user = user;
		this.store = store;
		this.description = description;
	}
	
	
	
}
