package com.soldesk6F.ondal.owner.sales.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.owner.sales.entity.Sales;

public interface SalesRepository extends JpaRepository<Sales, UUID> {
	
	
}
