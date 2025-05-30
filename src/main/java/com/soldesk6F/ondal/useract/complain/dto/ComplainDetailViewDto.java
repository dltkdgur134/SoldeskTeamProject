package com.soldesk6F.ondal.useract.complain.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ComplainDetailViewDto(
	    UUID complainId,
	    String complainTitle,
	    String complainContent,
	    String role,
	    LocalDateTime createdDate,
	    String complainStatus,
	    String userId,         // 또는 guestId
	    List<String> complainImgList,
	    List<ReplyDto> replyList
	) {
	    public record ReplyDto(
	        String replyContent,
	        String adminId,
	        LocalDateTime repliedDate
	    ) {}
	}
