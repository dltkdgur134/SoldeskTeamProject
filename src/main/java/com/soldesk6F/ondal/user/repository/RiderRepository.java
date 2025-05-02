package com.soldesk6F.ondal.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.user.entity.Rider;

public interface RiderRepository extends JpaRepository<Rider, UUID> {
	
	boolean existsByUser_UserId(String userId);
	Optional<Rider> findByUser_UserId(String userId);
	Optional<Rider> findByUser_UserUuid(UUID userUuid);
	
}
