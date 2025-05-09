package com.soldesk6F.ondal.search;

import java.util.List;

import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.store.dto.StoreDistanceProjection;
import com.soldesk6F.ondal.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreSearchService {

    private final StoreRepository storeRepository;

    public List<StoreDistanceProjection> searchByRadiusWithCond(double lat, double lon, double radiusKm , String keyword , StoreSortType sortType) {
    	
    	switch(sortType) {
    	
    	case DISTANCE:
            return storeRepository.findStoresWithinRadius(lat, lon, radiusKm,keyword);
    	case RATING:
    		return null;
    	case MIN_PRICE:
    		return null;
    	case DELIVERY_FEE:
    		return null;
    	case ORDER_AMOUNT:
    		return null;
    	default: throw new IllegalStateException("예상치 못한 정렬 타입: " + sortType);

    	
    	}

    	
 
    }
    
    
}
