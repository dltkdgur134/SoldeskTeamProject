package com.soldesk6F.ondal.useract.complain.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ComplainDto(
        UUID id,
        String title,
        String content,
        String userId,
        String role,
        LocalDateTime createdAt,
        String status,
        String images        // /uploads/complain/{file}
) {}