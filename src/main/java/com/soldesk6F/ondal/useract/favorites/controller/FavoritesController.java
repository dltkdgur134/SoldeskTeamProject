package com.soldesk6F.ondal.useract.favorites.controller;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.favorites.entity.Favorites;
import com.soldesk6F.ondal.useract.favorites.repository.FavoritesRepository;
import com.soldesk6F.ondal.useract.favorites.service.FavoritesService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FavoritesController {

	private final FavoritesService favoritesService;
	private final FavoritesRepository favoritesRepository;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;
	
	@PostMapping("/toggle/{storeId}")
	@ResponseBody
	public ResponseEntity<?> toggleFavorite(@PathVariable("storeId") UUID storeId, Principal principal) {
		String userId = principal.getName();
		User user = userRepository.findByUserId(userId).orElseThrow();
		Store store = storeRepository.findById(storeId).orElseThrow();

		Optional<Favorites> existing = favoritesRepository.findByUserAndStore(user, store);
		if (existing.isPresent()) {
			favoritesRepository.delete(existing.get());
			return ResponseEntity.ok(Map.of("status", "removed"));
		} else {
			Favorites favorite = Favorites.builder().user(user).store(store).build();
			favoritesRepository.save(favorite);
			return ResponseEntity.ok(Map.of("status", "added"));
		}
	}
	
	@PostMapping("/{storeId}")
	public ResponseEntity<?> addFavorite(@PathVariable("storeId") UUID storeId, Principal principal) {
		String userId = principal.getName();
		User user = userRepository.findByUserId(userId).orElseThrow();
		Store store = storeRepository.findById(storeId).orElseThrow();

		if (favoritesRepository.existsByUserAndStore(user, store)) {
			return ResponseEntity.badRequest().body("이미 찜한 가게입니다.");
		}

		Favorites favorite = Favorites.builder()
			.user(user)
			.store(store)
			.build();
		favoritesRepository.save(favorite);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{storeId}")
	public ResponseEntity<?> removeFavorite(@PathVariable("storeId") UUID storeId, Principal principal) {
		String userId = principal.getName();
		User user = userRepository.findByUserId(userId).orElseThrow();
		Store store = storeRepository.findById(storeId).orElseThrow();

		favoritesRepository.deleteByUserAndStore(user, store);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/favorites")
	public String showFavorites(Model model) {
		
		//model.addAttribute("favoriteStores" , favoritesService.);
		
		return "/content/favorites";
		
	}
	
	
	
	
	
	
	
	
}


