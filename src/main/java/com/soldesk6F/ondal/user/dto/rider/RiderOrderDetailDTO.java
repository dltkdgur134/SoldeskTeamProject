package com.soldesk6F.ondal.user.dto.rider;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
public class RiderOrderDetailDTO {
    private String orderId;
    private String storeName;
    private String storeAddress;
    private double storeLatitude;
    private double storeLongitude;
    private String deliveryAddress;
    private double deliveryAddressLatitude;
    private double deliveryAddressLongitude;
    private String orderTimeFormatted;
    private String deliveryRequest;
    private int deliveryFee;
    private String expectCookingTimeFormatted;
    private String orderToRider; // 예: 대기 중, 배정됨 등
}
