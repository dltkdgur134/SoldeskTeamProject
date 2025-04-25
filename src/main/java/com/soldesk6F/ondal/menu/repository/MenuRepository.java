package com.soldesk6F.ondal.menu.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.store.entity.Store;


public interface MenuRepository extends JpaRepository<Menu, UUID> {
	
	List<Menu> findByStore(Store store);
	List<Menu> findByStoreOrderByMenuOrderAsc(Store store);
	@Query("SELECT m FROM Menu m WHERE m.store = :store ORDER BY CASE WHEN m.menuOrder IS NULL THEN 1 ELSE 0 END, m.menuOrder ASC")
	List<Menu> findByStoreOrderByMenuOrderNullLast(@Param("store") Store store);
	List<Menu> findByStore_StoreId(UUID storeId);
	
	
}

