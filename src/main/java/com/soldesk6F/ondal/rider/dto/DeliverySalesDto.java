package com.soldesk6F.ondal.rider.dto;

import com.soldesk6F.ondal.rider.entity.DeliverySales;

import lombok.Data;

@Data
public class DeliverySalesDto {
    private String storeName;
    private int price;
    private int vat;
    private String date;
    private String status;

    public static DeliverySalesDto from(DeliverySales ds) {
        DeliverySalesDto dto = new DeliverySalesDto();
        dto.setStoreName(ds.getStore().getStoreName());
        dto.setPrice(ds.getDeliveryPrice());
        dto.setVat(ds.getDeliveryVat());
        dto.setDate(ds.getDeliverySalesDate().toString());
        dto.setStatus(ds.getDeliveryStatus().name());
        return dto;
    }
}

