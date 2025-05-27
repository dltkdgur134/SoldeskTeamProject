package com.soldesk6F.ondal.useract.complain.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ComplainAdminDto(
        UUID id,
        String title,
        String content,
        String userId,
        String role,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        String status,
        boolean hasImg
		) {}