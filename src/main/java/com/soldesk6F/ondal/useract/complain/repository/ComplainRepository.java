package com.soldesk6F.ondal.useract.complain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.useract.complain.dto.ComplainSearchCond;
import com.soldesk6F.ondal.useract.complain.entity.Complain;
import com.soldesk6F.ondal.useract.complain.entity.Complain.Role;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;




public interface ComplainRepository extends JpaRepository<Complain, UUID>,ComplainRepositoryCustom {
    List<Complain> findByGuestId(String guestId);
    List<Complain> findByComplainTitleContaining(String keyword);
    List<Complain> findByRole(Role role);
    List<Complain> findByUser_UserId(String userId);
}

