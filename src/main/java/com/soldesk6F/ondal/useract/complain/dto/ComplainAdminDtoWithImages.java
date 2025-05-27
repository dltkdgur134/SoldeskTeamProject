package com.soldesk6F.ondal.useract.complain.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ComplainAdminDtoWithImages(
	    UUID complainId,
	    String complainTitle,
	    String complainContent,
	    String userId,
	    String role,
	    LocalDateTime createdDate,
	    String complainStatus,
	    String firstImage,
	    List<String> images
	) {}