package com.soldesk6F.ondal.owner.sales.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoreSalesDto {
    private UUID storeId;
    private String storeName;
    private int totalSales;
    private String diffPercent; // 전월대비 증감율 (미구현 시 "0%" 고정)
}

