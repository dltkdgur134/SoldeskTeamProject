package com.soldesk6F.ondal.rider.dto;

import java.util.List;

import com.soldesk6F.ondal.rider.entity.DeliverySales;
import com.soldesk6F.ondal.rider.entity.RiderManagement;

import lombok.Data;

@Data
public class RiderManagementDto {
    private int totalSales;
    private int totalVat;
    private List<DeliverySalesDto> salesList;

    public static RiderManagementDto of(RiderManagement mgmt, List<DeliverySales> salesList) {
        RiderManagementDto dto = new RiderManagementDto();
        dto.setTotalSales(mgmt.getTotalSales());
        dto.setTotalVat(mgmt.getTotalVat());

        dto.setSalesList(salesList.stream().map(DeliverySalesDto::from).toList());
        return dto;
    }
}

