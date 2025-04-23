package com.soldesk6F.ondal.menu.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.store.entity.Store;


public interface MenuRepository extends JpaRepository<Menu, UUID> {
	
	List<Menu> findByStore(Store store);
	
}
