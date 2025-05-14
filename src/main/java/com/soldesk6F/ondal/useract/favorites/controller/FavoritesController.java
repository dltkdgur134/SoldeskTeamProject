package com.soldesk6F.ondal.useract.favorites.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk6F.ondal.useract.favorites.service.FavoritesService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FavoritesController {

	private final FavoritesService favoritesService;
	
	
	@GetMapping("/favorites")
	public String showFavorites(Model model) {
		
		
		
		model.addAttribute("favoriteStores");
		
		
		
		
		return "/content/favorites";
		
	}
	
	
	
	
	
	
	
	
}


