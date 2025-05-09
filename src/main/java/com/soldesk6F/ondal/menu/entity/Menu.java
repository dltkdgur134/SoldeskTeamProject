package com.soldesk6F.ondal.menu.entity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.store.entity.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
@Table(name = "menu", uniqueConstraints = { @UniqueConstraint(columnNames = { "menu_id", "menu_img" }) })
public class Menu {

	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "menu_id", nullable = false, unique = true)
	private UUID menuId;

	@ManyToOne
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(name = "menu_name", nullable = false, length = 15)
	private String menuName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private MenuCategory menuCategory;

	@Lob
	@Column(name = "description")
	private String description;

	@Column(name = "price", nullable = false)
	private int price;

	@Column(name = "menu_img", length = 255)
	private String menuImg;

	@Column(name = "menu_options1", length = 255)
	private String menuOptions1;

	@Column(name = "menu_options1_price", length = 255)
	private String menuOptions1Price;

	@Column(name = "menu_options2", length = 255)
	private String menuOptions2;

	@Column(name = "menu_options2_price", length = 255)
	private String menuOptions2Price;

	@Column(name = "menu_options3", length = 255)
	private String menuOptions3;

	@Column(name = "menu_options3_price", length = 255)
	private String menuOptions3Price;

	@Enumerated(EnumType.STRING)
	@Column(name = "menu_status", length = 20)
	private MenuStatus menuStatus;
	
	@Column(name = "menu_order")
	private Integer menuOrder;
	
	private UUID menuCategoryId;

	public enum MenuStatus {
		ACTIVE, INACTIVE, SOLD_OUT
	}

	// 기본 옵션 추가
	public String addDefaultOption(String optionsFromAdmin) {
		if (!optionsFromAdmin.contains("선택 안함")) {
			return optionsFromAdmin + ", 선택 안함";
		}
		return optionsFromAdmin;
	}

	// 옵션 이름들을 리스트로 변환
	public List<String> getMenuOptions1List() {
		return parseOptionNames(menuOptions1);
	}

	public List<String> getMenuOptions2List() {
		return parseOptionNames(menuOptions2);
	}

	public List<String> getMenuOptions3List() {
		return parseOptionNames(menuOptions3);
	}

	// 공통 처리 메서드
	private List<String> parseOptionNames(String optionString) {
		if (optionString == null || optionString.isBlank())
			return List.of();
		return Arrays.stream(optionString.split("@@__@@")).map(String::trim).collect(Collectors.toList());
	}

	// 옵션 가격을 리스트로 변환
	public List<Integer> getMenuOptions1PriceList() {
		return parseOptionPrices(menuOptions1Price);
	}

	public List<Integer> getMenuOptions2PriceList() {
		return parseOptionPrices(menuOptions2Price);
	}

	public List<Integer> getMenuOptions3PriceList() {
		return parseOptionPrices(menuOptions3Price);
	}

	private List<Integer> parseOptionPrices(String priceString) {
		if (priceString == null || priceString.isBlank())
			return List.of();
		return Arrays.stream(priceString.split("@@__@@")).map(String::trim).map(s -> s.isBlank() ? "0" : s)
				.map(Integer::parseInt).collect(Collectors.toList());
	}

	@Builder
	public Menu(Store store, String menuName, MenuCategory menuCategory, String description, int price, String menuImg,
			String menuOptions1, String menuOptions1Price, String menuOptions2, String menuOptions2Price,
			String menuOptions3, String menuOptions3Price, MenuStatus menuStatus) {
		this.store = store;
		this.menuName = menuName;
		this.menuCategory = menuCategory;
		this.description = description;
		this.price = price;
		this.menuImg = menuImg;
		this.menuOptions1 = menuOptions1;
		this.menuOptions1Price = menuOptions1Price;
		this.menuOptions2 = menuOptions2;
		this.menuOptions2Price = menuOptions2Price;
		this.menuOptions3 = menuOptions3;
		this.menuOptions3Price = menuOptions3Price;
		this.menuStatus = menuStatus != null ? menuStatus : MenuStatus.ACTIVE;
	}
	
	public String getMenuUuidAsString() {
	    return menuId != null ? menuId .toString() : null;
	}
	
}
