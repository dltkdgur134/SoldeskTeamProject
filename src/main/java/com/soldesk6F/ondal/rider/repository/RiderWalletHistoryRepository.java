package com.soldesk6F.ondal.rider.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.rider.entity.RiderWalletHistory;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RiderWalletHistoryRepository extends JpaRepository<RiderWalletHistory, UUID> {
    // 기본적인 CRUD 기능을 JPA가 제공하므로 추가적인 쿼리는 필요 없을 때는 빈 인터페이스로 유지
	int countByRider_RiderIdAndCreatedDateBetween(UUID riderId, LocalDateTime start, LocalDateTime end);

}
