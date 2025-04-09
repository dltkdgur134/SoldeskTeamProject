package com.soldesk6F.ondal.user.repository;

import com.soldesk6F.ondal.user.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface OwnerRepository extends JpaRepository<Owner, UUID> {

	boolean existsByUser_UserId(String userId);
    Optional<Owner> findByUser_UserId(String userId);
		
	
	
}
