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
public class RiderOrderMarkerDTO {
    private String orderId;
    private double storeLatitude;
    private double storeLongitude;
    private String storeName;
    private int deliveryFee;
    private String orderToRider;
}