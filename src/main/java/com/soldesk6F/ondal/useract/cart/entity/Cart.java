package com.soldesk6F.ondal.useract.cart.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.User;

import jakarta.persistence.CascadeType;
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
@Table(name = "cart")
public class Cart {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "cart_id",nullable = false , unique =  true)
	private UUID cartId;
	
	@OneToOne
	@JoinColumn(name = "user_uuid" , nullable = false, unique = true)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "store_id",nullable = false)
	private Store store;
	
	@CreationTimestamp
	@Column(name="created_date",nullable = false,updatable = false)
	private LocalDateTime createdDate;
	
	@UpdateTimestamp
	@Column(name="updated_date",nullable = false)
	private LocalDateTime updatedDate;

	@Builder
	public Cart(User user, Store store) {
		super();
		this.user = user;
		this.store = store;
	}
	
	 @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<CartItems> cartItems = new ArrayList<>();
	 
	    public int getTotalPrice() {
	    	return cartItems.stream()
	                .mapToInt(CartItems::getItemTotalPrice)  // 옵션 가격까지 합산된 가격을 사용
	                .sum();
	    }

	    public String getCartUuidAsString() {
		    return cartId != null ? cartId .toString() : null;
		}
	    
	    
}
