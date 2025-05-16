package com.soldesk6F.ondal.useract.favorites.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.favorites.entity.Favorites;

public interface FavoritesRepository extends JpaRepository<Favorites, UUID> {
	boolean existsByUserAndStore(User user, Store store);
	void deleteByUserAndStore(User user, Store store);
	Optional<Favorites> findByUserAndStore(User user, Store store);
	long countByStore(Store store);

	
	
	Optional<Favorites> findByUser_UserUuid(UUID userUUID);
	
	
	
	
	
}
