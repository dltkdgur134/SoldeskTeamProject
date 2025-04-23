package com.soldesk6F.ondal.menu.dto;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.soldesk6F.ondal.menu.entity.Menu.MenuStatus;

import lombok.Data;

@Data
public class MenuRegisterDto {

	private UUID menuId;
	private String menuName;
	private String description;
	private int price;
	private MultipartFile menuImg;
	
	private List<String> menuOptions1;
	private List<String> menuOptions1Price;
	
	private List<String> menuOptions2;
	private List<String> menuOptions2Price;
	
	private List<String> menuOptions3;
	private List<String> menuOptions3Price;

    private MenuStatus menuStatus;
}

