package com.soldesk6F.ondal.useract.complain.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.soldesk6F.ondal.useract.complain.dto.ComplainAdminDto;
import com.soldesk6F.ondal.useract.complain.dto.ComplainSearchCond;
import com.soldesk6F.ondal.useract.complain.dto.ReplySavedDto;

public interface ComplainInterface {
	Page<ComplainAdminDto> list(ComplainSearchCond cond, Pageable pageable);
	ReplySavedDto reply(UUID complainId, String adminId,
			String content, List<MultipartFile> images) throws IOException;
}