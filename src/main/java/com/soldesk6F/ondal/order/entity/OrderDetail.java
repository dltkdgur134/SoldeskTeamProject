package com.soldesk6F.ondal.order.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.rider.entity.DeliverySales;
import com.soldesk6F.ondal.rider.entity.RiderManagement;
import com.soldesk6F.ondal.rider.entity.DeliverySales.DeliveryStatus;
import com.soldesk6F.ondal.store.entity.Store;

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
@Table(name = "order_detail")
public class OrderDetail {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "order_detail_id" ,nullable = false, unique = true)
	private UUID orderDetailId;
	
	@ManyToOne
	@JoinColumn(name = "order_id" , nullable = false)
	private Order order;
	
	@ManyToOne
	@JoinColumn(name = "menu_id" , nullable = false)
	private Menu menu;
	
	@Column(nullable = false)
	private int quantity;
	
	@Column(nullable = false)
	private int price;
	
	@Builder
	public OrderDetail(Order order, Menu menu, int quantity, int price) {
		super();
		this.order = order;
		this.menu = menu;
		this.quantity = quantity;
		this.price = price;
	}
	
	public void setPrice(int price) {
	    if (price < 0) {
	        throw new IllegalArgumentException("Price cannot be negative");
	    }
	    this.price = price;
	}
	
	
}
