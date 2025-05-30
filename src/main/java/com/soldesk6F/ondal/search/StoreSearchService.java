package com.soldesk6F.ondal.search;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.store.dto.StoreDistanceProjection;
import com.soldesk6F.ondal.store.entity.StoreDto;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.user.controller.user.UpdateUserController;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.regAddress.repository.RegAddressRepository;
import com.soldesk6F.ondal.useract.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreSearchService {


    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    
    
    
    public List<StoreDto> searchByRadiusWithCond(int radiusMeters , String original ,String bestMatcher, StoreSortType sortType, int page , int size,String category ) {
    	
    	
    	


        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() &&
        	    !(auth.getPrincipal() instanceof String)) {
        	CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        	User user = userDetails.getUser();
        	User dbUser = userRepository.getById(user.getUserUuid());
        	if(dbUser != null) {
        		double lat = dbUser.getUserSelectedAddress().getUserAddressLatitude();
        		double lon = dbUser.getUserSelectedAddress().getUserAddressLongitude();
        		System.out.println(lat);
        		System.out.println(lon);
 
        
       
        
        double degLat = radiusMeters / 111_319.9;
        double degLon = radiusMeters / (111_319.9 * Math.cos(Math.toRadians(lat)));

        double minLat = lat - degLat;
        double maxLat = lat + degLat;
        double minLon = lon - degLon;
        double maxLon = lon + degLon;
        

        String bbox = String.format(
        	    "POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))",
        	    minLon, minLat,   // 좌하 (lon, lat)
        	    maxLon, minLat,   // 우하
        	    maxLon, maxLat,   // 우상
        	    minLon, maxLat,   // 좌상
        	    minLon, minLat    // 닫기
        	);
        
        System.out.println(bbox);
       
        // 2. Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        if(category.equals("all")) {
        
    	switch(sortType) {
    	
    	case DISTANCE:
    	    return storeRepository.searchNearbyStoresByDistance(
                    lon,               // 기준 경도
                    lat,               // 기준 위도
                    bbox,              // ← WKT 바운딩 박스
                    radiusMeters,      // 반경 (m)
                    original,
                    bestMatcher,
                    pageable)
               .stream()
               .map(store -> {
       			double distance = calculateDistance(
						lat, lon,
						store.getStoreLatitude(), store.getStoreLongitude()
					);
                   String imageUrl = (store.getBrandImg() != null && !store.getBrandImg().isBlank())
                                     ? store.getBrandImg()
                                     : "/img/store/default.png";

                   double avgRating = reviewRepository.findAverageRatingByStore(store);
                   long reviewCount = reviewRepository.countByStore(store);

                   return StoreDto.builder()
                                  .storeId(store.getStoreId())
                                  .storeName(store.getStoreName())
                                  .category(store.getCategory())
                                  .storePhone(store.getStorePhone())
                                  .storeAddress(store.getStoreAddress())
                                  .storeIntroduce(store.getStoreIntroduce())
                                  .storeStatus(store.getStoreStatus().name())
                                  .imageUrl(imageUrl)
                                  .avgRating(avgRating)
                                  .reviewCount(reviewCount)
                                  .distanceInKm(distance)
                                  .build();
               })
               .toList();
    			
    	default: throw new IllegalStateException("예상치 못한 정렬 타입: " + sortType);

    		
    	}
        }else {
        	
    	    return storeRepository.searchNearbyStoresByDistanceWithCategory(
                    lon,               // 기준 경도
                    lat,               // 기준 위도
                    bbox,              // ← WKT 바운딩 박스
                    radiusMeters,      // 반경 (m)
                    original,
                    bestMatcher,
                    category,
                    pageable
                    )
               .stream()
               .map(store -> {
       			double distance = calculateDistance(
						lat, lon,
						store.getStoreLatitude(), store.getStoreLongitude()
					);
                   String imageUrl = (store.getBrandImg() != null && !store.getBrandImg().isBlank())
                                     ? store.getBrandImg()
                                     : "/img/store/default.png";

                   double avgRating = reviewRepository.findAverageRatingByStore(store);
                   long reviewCount = reviewRepository.countByStore(store);

                   return StoreDto.builder()
                                  .storeId(store.getStoreId())
                                  .storeName(store.getStoreName())
                                  .category(store.getCategory())
                                  .storePhone(store.getStorePhone())
                                  .storeAddress(store.getStoreAddress())
                                  .storeIntroduce(store.getStoreIntroduce())
                                  .storeStatus(store.getStoreStatus().name())
                                  .imageUrl(imageUrl)
                                  .avgRating(avgRating)
                                  .reviewCount(reviewCount)
                                  .distanceInKm(distance)
                                  .build();
               })
               .toList();
        	
        	
        }
        	}
        	throw new IllegalArgumentException("사용자가 없습니다");
        	}else {
        		throw new IllegalArgumentException("사용자가 없습니다");
        	}
    	
 
    }
    
	private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		// lat1, lon1 - 사용자의 위도, 경도 / lat2, lon2 - 가게의 위도, 경도
		final int R = 6371; // 한국 기준 지구 반지름 (km)
		double latDistance = toRadians(lat2 - lat1); // 위도 차이 (Δφ)
		double lonDistance = toRadians(lon2 - lon1); // 경도 차이 (Δλ)
		// a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
		double a = sin(latDistance / 2) * sin(latDistance / 2)
			+ cos(toRadians(lat1)) * cos(toRadians(lat2))
			* sin(lonDistance / 2) * sin(lonDistance / 2);
		double c = 2 * atan2(sqrt(a), sqrt(1 - a)); // 지구 중심을 기준으로 두 지점 사이의 중심각
		double distance = R * c; // 가게와 유저 주소 간의 거리
		
		return Math.round(distance * 10) / 10.0; // 소수점 1자리까지 반올림
	}
    
    
}
