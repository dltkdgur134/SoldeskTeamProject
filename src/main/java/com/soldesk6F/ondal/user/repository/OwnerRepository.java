package com.soldesk6F.ondal.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.User;

public interface OwnerRepository extends JpaRepository<Owner, UUID> {

	boolean existsByUser_UserId(String userId);
    Optional<Owner> findByUser_UserId(String userId);
		
	
	
}
