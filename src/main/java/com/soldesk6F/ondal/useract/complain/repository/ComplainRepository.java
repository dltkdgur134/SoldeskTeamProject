package com.soldesk6F.ondal.useract.complain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.useract.complain.dto.ComplainDto;
import com.soldesk6F.ondal.useract.complain.dto.ComplainSearchCond;
import com.soldesk6F.ondal.useract.complain.entity.Complain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;




public interface ComplainRepository extends JpaRepository<Complain, UUID>,ComplainRepositoryCustom {
    List<Complain> findByGuestId(String guestId);

}

