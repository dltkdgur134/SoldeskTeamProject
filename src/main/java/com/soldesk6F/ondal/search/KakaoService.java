//package com.soldesk6F.ondal.search;
//
//
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriUtils;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class KakaoService {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @Value("${kakao.rest-key}")
//    private String kakaoRestKey;
//
//    public LatLng toLatLng(String address) {
//        String url = "https://dapi.kakao.com/v2/local/search/address.json?query="
//                   + UriUtils.encode(address, StandardCharsets.UTF_8);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "KakaoAK " + kakaoRestKey);   // ← prefix 포함
//
//        ResponseEntity<KakaoAddressResponse> resp = restTemplate.exchange(
//                url, HttpMethod.GET, new HttpEntity<>(headers), KakaoAddressResponse.class);
//
//        KakaoAddressResponse body = resp.getBody();
//        if (body == null || body.getDocuments().isEmpty())
//            throw new IllegalStateException("좌표 변환 실패 - 결과 없음");
//
//        var doc = body.getDocuments().get(0);
//        return new LatLng(
//                Double.parseDouble(doc.getY()),   
//                Double.parseDouble(doc.getX()));  
//    }
//}
