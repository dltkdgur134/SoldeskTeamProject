package com.soldesk6F.ondal.useract.complain.service;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.soldesk6F.ondal.admin.entity.Admin;
import com.soldesk6F.ondal.admin.repository.AdminRepository;
import com.soldesk6F.ondal.useract.complain.dto.ComplainAdminDto;
import com.soldesk6F.ondal.useract.complain.dto.ComplainSearchCond;
import com.soldesk6F.ondal.useract.complain.dto.ReplySavedDto;
import com.soldesk6F.ondal.useract.complain.entity.Complain;
import com.soldesk6F.ondal.useract.complain.entity.ComplainReply;
import com.soldesk6F.ondal.useract.complain.repository.ComplainRepository;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class ComplainServiceImpl  implements ComplainInterface {

    private final ComplainRepository complainRepo;
    private final AdminRepository    adminRepo;
    private final FileStore          fileStore;

    /* 목록 ------------------------------------------------------------ */
    @Override
    public Page<ComplainAdminDto> list(ComplainSearchCond cond, Pageable pageable) {
        return complainRepo.search(cond, pageable);
    }

    /* 답변 ------------------------------------------------------------ */
    @Override 
    @Transactional
    public ReplySavedDto reply(UUID complainId, String adminId,
                               String content, List<MultipartFile> images) throws IOException {

        Complain complain = complainRepo.findById(complainId)
                .orElseThrow(() -> new NoSuchElementException("Complain not found"));
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new NoSuchElementException("Admin not found"));

        ComplainReply reply = ComplainReply.builder()
                .complain(complain)
                .admin(admin)
                .replyContent(content)
                .build();

        if (images != null) {
            for (MultipartFile f : images) {
                if (f.isEmpty()) continue;
                String stored = fileStore.save(f, "reply");
				/*
				 * reply.getImages().add( ComplainReplyImg.builder() .reply(reply)
				 * .replyImg(stored) .build());
				 */
            }
        }

        complain.setComplainStatus(Complain.ComplainStatus.RESOLVED);
        complain.getReplies().add(reply);   // cascade = ALL

        return new ReplySavedDto(reply.getComplainReplyId(), reply.getRepliedDate());
    }
}
