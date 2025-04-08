package com.soldesk6F.ondal.useract.searchHistory.SearchHistoryRepository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.searchHistory.entity.SearchHistory;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, UUID> {
    boolean existsByUserAndSearchName(User user, String searchName);
}
