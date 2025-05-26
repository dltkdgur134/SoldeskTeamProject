package com.soldesk6F.ondal.useract.complain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.useract.complain.entity.ComplainImg;

public interface ComplainImgRepository extends JpaRepository<ComplainImg, UUID> {

}
