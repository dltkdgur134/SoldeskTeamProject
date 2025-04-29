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

    public List<StoreDistanceProjection> searchByRadiusWithCond(double lat, double lon, double radiusKm , String keyword) {
        return storeRepository.findStoresWithinRadius(lat, lon, radiusKm,keyword);
    }
    
    
}
