package com.soldesk6F.ondal.useract.favorites.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.useract.favorites.entity.Favorites;
import com.soldesk6F.ondal.useract.favorites.repository.FavoritesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoritesService {

	private final FavoritesRepository favoritesRepository;
	
	
	
	public List<Favorites> getUserFavorites(){
		CustomUserDetails cud;
		if(SecurityContextHolder.getContext().getAuthentication() instanceof CustomUserDetails instanceCud) {
			cud = instanceCud;
		}
		favoritesRepository.findByUser_UserUuid(cud.getUser());
		
		
		
		
	}
	
	
	
	
	
	
	
	
}
