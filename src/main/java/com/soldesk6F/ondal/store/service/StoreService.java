package com.soldesk6F.ondal.store.service;

import com.soldesk6F.ondal.store.entity.StoreDto;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public List<StoreDto> getStoresByCategory(String category) {
    	
		log.info("카테고리로 가게 검색 요청: {}", category);
		
		List<Store> stores = storeRepository.findByCategory(category);
		
		log.info("조회된 가게 수: {}", stores.size());
    	
    	return storeRepository.findByCategory(category).stream()
    	        .map(store -> {
    	            StoreDto dto = StoreDto.builder()
    	                    .storeName(store.getStoreName())
    	                    .category(store.getCategory())
    	                    .storePhone(store.getStorePhone())
    	                    .storeAddress(store.getStoreAddress())
    	                    .storeIntroduce(store.getStoreIntroduce())
    	                    .storeStatus(store.getStoreStatus().getDescription())
    	                    .imageUrl(store.getBrandImgs() != null && !store.getBrandImgs().isEmpty()
    	                            ? store.getBrandImgs().get(0).getBrandImgFilePath() : "/img/default.png")
    	                    .build();
    	            return dto;
    	        })
    	        .collect(Collectors.toList());
    }
}
