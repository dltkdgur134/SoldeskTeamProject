package com.soldesk6F.ondal.search;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum StoreSortType {
    DISTANCE("distance", "거리순"),
    RATING("rating", "별점순"),
    MIN_PRICE("minPrice", "최소금액순"),
    DELIVERY_FEE("deliveryFee", "배달비 낮은순"),
    ORDER_AMOUNT("orderAmount", "최소주문금액순");

    private final String code;     // 파라미터 값
    private final String label;    // 사용자에게 보여주는 한글명

    public static StoreSortType from(String code) {
        return Arrays.stream(values())
                .filter(type -> type.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid sortType: " + code));
    }
}
