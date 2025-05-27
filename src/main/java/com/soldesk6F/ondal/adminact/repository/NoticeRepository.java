package com.soldesk6F.ondal.adminact.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soldesk6F.ondal.adminact.entity.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, UUID> {
	
	
}
