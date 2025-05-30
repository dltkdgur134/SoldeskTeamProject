package com.soldesk6F.ondal.rider.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.rider.entity.RiderManagement;
import com.soldesk6F.ondal.user.entity.Rider;


public interface RiderManagementRepository extends JpaRepository<RiderManagement, UUID> {
    
    // 라이더 ID로 RiderManagement 조회
	Optional<RiderManagement> findByRider(Rider rider);
}