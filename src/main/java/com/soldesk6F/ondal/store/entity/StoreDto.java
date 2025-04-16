package com.soldesk6F.ondal.store.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreDto {
    private String storeName;
    private String category;
    private String storePhone;
    private String storeAddress;
    private String storeIntroduce;
    private String storeStatus;
    private String imageUrl;
}

