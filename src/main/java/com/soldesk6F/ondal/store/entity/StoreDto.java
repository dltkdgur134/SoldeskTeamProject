package com.soldesk6F.ondal.store.entity;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreDto {
	private UUID storeId;
    private String storeName;
    private String category;
    private String storePhone;
    private String storeAddress;
    private String storeIntroduce;
    private String storeStatus;
    private String imageUrl;
    private double avgRating;
	private long reviewCount;
	private double distanceInKm; // 가게와 유저간의 거리
}

