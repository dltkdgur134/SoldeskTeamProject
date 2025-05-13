package com.soldesk6F.ondal.useract.favorites.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.useract.favorites.entity.Favorites;

public interface FavoritesRepository extends JpaRepository<Favorites, UUID> {

}
