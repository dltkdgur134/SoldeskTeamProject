package com.soldesk6F.ondal.useract.complain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.useract.complain.entity.Complain;
import com.soldesk6F.ondal.useract.complain.entity.ComplainImg;

public interface ComplainImgRepository extends JpaRepository<ComplainImg, UUID> {
	List<ComplainImg> findByComplain_ComplainId(UUID complainId);

	List<ComplainImg> findByComplain(Complain complain);
}
