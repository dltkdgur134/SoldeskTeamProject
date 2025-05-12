package com.soldesk6F.ondal.search;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KakaoAddressResponse {

    private List<Document> documents;

    @Getter
    @Setter
    public static class Document {
        private String x; // 경도
        private String y; // 위도
    }
}