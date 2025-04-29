package com.soldesk6F.ondal.menu.dto;

import com.soldesk6F.ondal.menu.entity.MenuCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryDto {

	private UUID id;
	private String categoryName;

	public MenuCategoryDto(MenuCategory category) {
		this.id = category.getId();
		this.categoryName = category.getCategoryName();
	}

	public UUID getId() {
		return id;
	}

	public String getCategoryName() {
		return categoryName;
	}
	
}



