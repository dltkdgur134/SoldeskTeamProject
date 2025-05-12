package com.soldesk6F.ondal.owner.order.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StatusTimeline {
    private String status;
    private LocalDateTime timestamp;

    public StatusTimeline(String status, LocalDateTime timestamp) {
        this.status = status;
        this.timestamp = timestamp;
    }
    // + getters / setters
}