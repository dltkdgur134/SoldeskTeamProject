package com.soldesk6F.ondal.rider.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.rider.entity.RiderWalletHistory;
import com.soldesk6F.ondal.rider.repository.RiderWalletHistoryRepository;
import com.soldesk6F.ondal.user.entity.Rider;

@Service
public class RiderWalletHistoryService {

    private final RiderWalletHistoryRepository riderWalletHistoryRepository;

    @Autowired
    public RiderWalletHistoryService(RiderWalletHistoryRepository riderWalletHistoryRepository) {
        this.riderWalletHistoryRepository = riderWalletHistoryRepository;
    }

    @Transactional
    public void saveWalletHistory(Rider rider, int amount, int fee, int finalAmount, String description) {
        // 출금 히스토리 엔티티 생성
        RiderWalletHistory riderWalletHistory = RiderWalletHistory.builder()
                .riderWalletHistoryId(UUID.randomUUID()) // UUID 생성
                .rider(rider)
                .amount(amount)
                .fee(fee)
                .finalAmount(finalAmount)
                .description(description)
                .build();

        // 출금 히스토리 저장
        riderWalletHistoryRepository.save(riderWalletHistory);
    }
}

