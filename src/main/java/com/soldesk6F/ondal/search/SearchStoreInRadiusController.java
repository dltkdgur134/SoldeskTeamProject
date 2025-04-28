//package com.soldesk6F.ondal.search;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.client.RestTemplate;
//
//import com.soldesk6F.ondal.store.entity.Store;
//import com.soldesk6F.ondal.store.repository.StoreRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/api")          // prefix 하나 잡아두면 URL 정리 쉬움
//public class SearchStoreInRadiusController {
//
//    private final KakaoService kakaoService;
//    private final StoreRepository storeRepository;
//
//    /** 예: GET /api/stores?address=서울+역삼동&keyword=떡볶이&radius=1 */
//    @GetMapping("/stores")
//    @ResponseBody
//    public ResponseEntity<?> listStores(
//            @RequestParam String address,
//            @RequestParam(required = false) String keyword,
//            @RequestParam(defaultValue = "1.0") double radius   // km
//    ) {
//        // 1. 주소 → 위·경도
//        LatLng latLng = kakaoService.toLatLng(address);
//
//        // 2. 반경 검색
//        List<Store> stores =
//            StoreRepository.findWithinRadius(
//                    latLng.getLatitude(),
//                    latLng.getLongitude(),
//                    radius,
//                    (keyword == null || keyword.isBlank()) ? null : keyword.trim());
//
//        return ResponseEntity.ok(stores);      // → JSON 배열 반환
//    }
//}
//
//
