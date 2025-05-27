package com.soldesk6F.ondal.useract.complain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReplySavedDto(
        UUID id,
        LocalDateTime repliedAt
) {}