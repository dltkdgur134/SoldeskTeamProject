package com.soldesk6F.ondal.useract.complain.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk6F.ondal.useract.complain.dto.ComplainAdminDtoWithImages;
import com.soldesk6F.ondal.useract.complain.entity.Complain;
import com.soldesk6F.ondal.useract.complain.entity.ComplainImg;
import com.soldesk6F.ondal.useract.complain.repository.ComplainRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/complains")
public class AdminComplainController {

	private final ComplainRepository complainRepository;

	
	
	@GetMapping("/{id}")
	public ResponseEntity<ComplainAdminDtoWithImages> detail(@PathVariable("id") UUID id) {
	    Complain complain = complainRepository.findById(id).get();
	    if(complain!=null) {
	    List<String> imageNames = complain.getComplainImgs().stream().map(ComplainImg::getComplainImg).toList();

	    ComplainAdminDtoWithImages dto = new ComplainAdminDtoWithImages(
	        complain.getComplainId(),
	        complain.getComplainTitle(),
	        complain.getComplainContent(),
	        complain.getUser() != null ? complain.getUser().getUserId() : complain.getGuestId(),
	        complain.getRole().name(),
	        complain.getCreatedDate(),
	        complain.getComplainStatus().name(),
	        imageNames
	    );
	    return ResponseEntity.ok(dto);
	    }else {
	    	throw new IllegalArgumentException("해당 문의 없음");
	    }
	}
	
	
	
	
}
