package com.soldesk6F.ondal.useract.favorites.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteStoreDto {

    /** 매장 PK (상세 페이지 링크용) */
    private Long id;

    private String name;

    private String imageUrl;

    private String category;

    private Double avgRating;
//
    private Long reviewCount;
//
    private String MinimumPay;
    
}



