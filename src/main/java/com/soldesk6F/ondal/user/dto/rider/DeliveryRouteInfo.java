package com.soldesk6F.ondal.user.dto.rider;

import java.util.List;
import java.util.Map;

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
public class DeliveryRouteInfo {
    private double riderLat;
    private double riderLng;
    private double storeLat;
    private double storeLng;
    private double endLat;
    private double endLng;
    private List<Map<String, Double>> path1;
    private List<Map<String, Double>> path2;
    private Map<String, Object> summary;

}
