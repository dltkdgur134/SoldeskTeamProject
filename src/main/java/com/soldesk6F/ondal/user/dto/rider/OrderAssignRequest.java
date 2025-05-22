package com.soldesk6F.ondal.user.dto.rider;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAssignRequest {

    private UUID orderId;
    private int expectMinute;
    private int expectSecond;

    // getters and setters
}

