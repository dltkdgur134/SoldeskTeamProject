package com.soldesk6F.ondal.owner.wallet.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.soldesk6F.ondal.owner.wallet.entity.OwnerWalletHistory;

public interface OwnerWalletHistoryRepository extends JpaRepository<OwnerWalletHistory, UUID> {
    // 기본적인 CRUD 기능을 JPA가 제공하므로 추가적인 쿼리는 필요 없을 때는 빈 인터페이스로 유지
	int countByOwner_OwnerIdAndCreatedDateBetween(UUID ownerId, LocalDateTime start, LocalDateTime end);

}
