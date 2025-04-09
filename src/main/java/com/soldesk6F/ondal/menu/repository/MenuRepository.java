package com.soldesk6F.ondal.menu.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.menu.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
	
	
	
}
