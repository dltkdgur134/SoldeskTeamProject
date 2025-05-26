package com.soldesk6F.ondal.useract.complain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.useract.complain.entity.Complain;
import com.soldesk6F.ondal.useract.complain.entity.Complain.Role;

import java.util.List;
import java.util.UUID;

public interface ComplainRepository extends JpaRepository<Complain, UUID> {
    List<Complain> findByGuestId(String guestId);
    List<Complain> findByComplainTitleContaining(String keyword);
    List<Complain> findByRole(Role role);
    List<Complain> findByUser_UserId(String userId);
}

