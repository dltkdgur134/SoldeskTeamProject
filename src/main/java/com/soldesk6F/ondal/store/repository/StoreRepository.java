package com.soldesk6F.ondal.store.repository;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.Owner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
	List<Store> findByCategory(String category);
	List<Store> findByOwner(Owner owner);
	 boolean existsByOwner_OwnerId(UUID ownerId);
	 Store findByStoreId(UUID StoreId);
}






