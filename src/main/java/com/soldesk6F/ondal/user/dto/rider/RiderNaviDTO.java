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
public class RiderNaviDTO {

	private String orderId;
    private double storeLatitude;
    private double storeLongitude;
    private double deliveryAddressLatitude;
    private double deliveryAddressLongitude;
}
