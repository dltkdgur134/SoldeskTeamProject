package com.soldesk6F.ondal.user.dto.rider;

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
public class RiderStatusResponse {
    private String riderStatus;
    private String riderStatusDescription;

}