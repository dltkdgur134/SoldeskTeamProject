package com.soldesk6F.ondal.user.dto;

import lombok.Data;

@Data
public class RiderForm {
    private String secondaryPassword;
    private String vehicleNumber;
    private String riderHubAddress;
    private String riderPhone;
    private String deliveryRange;
    private String riderNickname;
    private double hubAddressLatitude;
    private double hubAddressLongitude;
    
}
