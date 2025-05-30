package com.soldesk6F.ondal.store.dto;

/** 반경 검색 결과(아이디‧이름‧거리)만 가져오는 프로젝션 */
public interface StoreDistanceProjection {
    java.util.UUID getId();
    String         getName();
    Double         getDistanceMeters();   // 메서드명 = SQL alias
}
