package com.soldesk6F.ondal.search;

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
    
    
    
    public List<StoreDto> searchByRadiusWithCond(int radiusMeters , String original ,String bestMatcher, StoreSortType sortType, int page , int size ) {
    	
    	
    	


        double lat=0,lon=0;
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() &&
        	    !(auth.getPrincipal() instanceof String)) {
        	CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        	User user = userDetails.getUser();
        	User dbUser = userRepository.getById(user.getUserUuid());
        	if(dbUser != null) {
        		lat = dbUser.getUserSelectedAddress().getUserAddressLatitude();
        		lon = dbUser.getUserSelectedAddress().getUserAddressLongitude();
        	}
        	}
        
       
        
        double degreeLat = radiusMeters / 111319.9;
        double degreeLon = radiusMeters / (111319.9 * Math.cos(Math.toRadians(lat)));
        double minLat = lat - degreeLat;
        double maxLat = lat + degreeLat;
        double minLon = lon - degreeLon;
        double maxLon = lon + degreeLon;
        
        String bbox = String.format(
                "POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))",
                minLon, minLat,
                maxLon, minLat,
                maxLon, maxLat,
                minLon, maxLat,
                minLon, minLat
        );
        // 2. Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);
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
                                  .build();
               })
               .toList();
    			
    	default: throw new IllegalStateException("예상치 못한 정렬 타입: " + sortType);

    	
    	}

    	
 
    }
    
    
}
