package com.soldesk6F.ondal.owner.wallet.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.owner.wallet.entity.OwnerWalletHistory;
import com.soldesk6F.ondal.owner.wallet.repository.OwnerWalletHistoryRepository;
import com.soldesk6F.ondal.user.entity.Owner;

import jakarta.transaction.Transactional;


@Service
public class OwnerWalletHistoryService {

    private final OwnerWalletHistoryRepository ownerWalletHistoryRepository;

    @Autowired
    public OwnerWalletHistoryService(OwnerWalletHistoryRepository ownerWalletHistoryRepository) {
        this.ownerWalletHistoryRepository = ownerWalletHistoryRepository;
    }

    @Transactional
    public void saveWalletHistory(Owner owner, int amount, int fee, int finalAmount, String description) {
        // 출금 히스토리 엔티티 생성
        OwnerWalletHistory ownerWalletHistory = OwnerWalletHistory.builder()
                .ownerWalletHistoryId(UUID.randomUUID()) // UUID 생성
                .owner(owner)
                .amount(amount)
                .fee(fee)
                .finalAmount(finalAmount)
                .description(description)
                .build();

        // 출금 히스토리 저장
        ownerWalletHistoryRepository.save(ownerWalletHistory);
    }
}

