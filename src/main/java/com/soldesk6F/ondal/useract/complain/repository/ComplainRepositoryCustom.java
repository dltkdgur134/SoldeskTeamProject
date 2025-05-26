package com.soldesk6F.ondal.useract.complain.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.soldesk6F.ondal.useract.complain.dto.ComplainDto;
import com.soldesk6F.ondal.useract.complain.dto.ComplainSearchCond;

public interface ComplainRepositoryCustom {
    Page<ComplainDto> search(ComplainSearchCond cond, Pageable pageable);
}