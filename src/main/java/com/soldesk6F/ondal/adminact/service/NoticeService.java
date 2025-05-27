package com.soldesk6F.ondal.adminact.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.adminact.entity.Notice;
import com.soldesk6F.ondal.adminact.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

	private final NoticeRepository noticeRepository;
	
	public Notice save(Notice notice) {
        return noticeRepository.save(notice);
    }
	// 공지 전체 조회 (목록)
    public List<Notice> findAll() {
        return noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate"));
    }

    // 공지 단건 조회
    public Optional<Notice> findById(UUID id) {
        return noticeRepository.findById(id);
    }

    // 공지 삭제
    public void deleteById(UUID id) {
        noticeRepository.deleteById(id);
    }
	
}
