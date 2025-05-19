package com.soldesk6F.ondal.useract.favorites.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.favorites.dto.FavoriteStoreDto;
import com.soldesk6F.ondal.useract.favorites.entity.Favorites;
import com.soldesk6F.ondal.useract.favorites.repository.FavoritesRepository;
import com.soldesk6F.ondal.useract.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoritesService {

	private final FavoritesRepository favoritesRepository;
	private final ReviewRepository reviewRepository;
	
	
	
	public List<FavoriteStoreDto> getUserFavorites(){
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		CustomUserDetails cud = null;
		if (principal instanceof CustomUserDetails instanceCud) {
		    cud = instanceCud;
		}
		if(cud != null) {
			User user = cud.getUser();
			List<Favorites> listFavorites = favoritesRepository.findByUser_UserUuid(user.getUserUuid());
			List<FavoriteStoreDto> fsdList = new ArrayList<>();
			if(listFavorites!=null) {
				
				for(Favorites favorites : listFavorites) {
					FavoriteStoreDto fsdBuffer =  FavoriteStoreDto.builder().avgRating(reviewRepository.findAverageRatingByStore(favorites.getStore()))
					.reviewCount(reviewRepository.countByStore(favorites.getStore()))
					.category(favorites.getStore().getCategory())
					.imageUrl(favorites.getStore().getBrandImg())
					.name(favorites.getStore().getStoreName())
					.id(favorites.getStore().getStoreId())
					.build();
					fsdList.add(fsdBuffer);
					
					}
			return fsdList;
			}
			
			}
		
		throw new IllegalArgumentException("해당하는 유저가없습니다");
	}
	
	
	
	
	
	
	
	
}
