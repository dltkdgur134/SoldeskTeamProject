package com.soldesk6F.ondal.menu.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.menu.entity.MenuCategory;
import com.soldesk6F.ondal.store.entity.Store;


public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID> {
    List<MenuCategory> findByStore(Store store);
    Optional<MenuCategory> findByStoreAndCategoryName(Store store, String categoryName);
    boolean existsByStoreAndCategoryName(Store store, String categoryName);
    void deleteAllByStore(Store store);
}
