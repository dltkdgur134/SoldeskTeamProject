package com.soldesk6F.ondal.useract.order.entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soldesk6F.ondal.menu.entity.Menu;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
	@JsonIgnore
	private Order order;
	
	@ManyToOne
	@JoinColumn(name = "menu_id" , nullable = false)
	private Menu menu;
	
	@ElementCollection
	@CollectionTable(name = "order_detail_option_names", joinColumns = @JoinColumn(name = "order_detail_id"))
	@Column(name = "option_name")
	private List<String> optionNames;
	
	@ElementCollection
	@CollectionTable(name = "order_detail_option_prices", joinColumns = @JoinColumn(name = "order_detail_id"))
	@Column(name = "option_price")
	private List<Integer> optionPrices;
	
	@Column(nullable = false)
	private int quantity;
	
	@Column(nullable = false)
	private int price;
	
	@Builder
	public OrderDetail(Order order, Menu menu, int quantity, int price,
	                   List<String> optionNames, List<Integer> optionPrices) {
	    this.order = order;
	    this.menu = menu;
	    this.quantity = quantity;
	    this.price = calculateTotalPrice();
	    this.optionNames = optionNames;
	    this.optionPrices = optionPrices;
	}
	
	public void setPrice(int price) {
	    if (price < 0) {
	        throw new IllegalArgumentException("Price cannot be negative");
	    }
	    this.price = price;
	}
	
	public int calculateTotalPrice() {
	    int optionsTotal = optionPrices != null
	        ? optionPrices.stream().mapToInt(Integer::intValue).sum()
	        : 0;
	    return (menu.getPrice() + optionsTotal) * quantity;
	}
	
	public String getOrderDetailUuidAsString() {
	    return orderDetailId != null ? orderDetailId .toString() : null;
	}
	
}
