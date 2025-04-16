package com.soldesk6F.ondal.store.repository;

import com.soldesk6F.ondal.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
	List<Store> findByCategory(String category);
}






