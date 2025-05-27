package com.soldesk6F.ondal.useract.complain.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.soldesk6F.ondal.useract.complain.dto.ComplainAdminDto;
import com.soldesk6F.ondal.useract.complain.dto.ComplainSearchCond;

public interface ComplainRepositoryCustom {
    Page<ComplainAdminDto> search(ComplainSearchCond cond, Pageable pageable);
}