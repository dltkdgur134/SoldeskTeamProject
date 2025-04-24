package com.soldesk6F.ondal.menu.dto;

import java.util.List;
import java.util.UUID;

import com.soldesk6F.ondal.menu.entity.Menu.MenuStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuDto {
	private UUID menuId;
	private UUID storeId;
	private String menuName;
	private String description;
	private int price;
	private String menuImg;
	private String menuCategory;
	private UUID menuCategoryId;
	
	private List<String> menuOptions1;
	private List<Integer> menuOptions1Price;
	private List<String> menuOptions2;
	private List<Integer> menuOptions2Price;
	private List<String> menuOptions3;
	private List<Integer> menuOptions3Price;

	private MenuStatus menuStatus;
}

