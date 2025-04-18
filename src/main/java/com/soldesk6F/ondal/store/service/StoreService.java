package com.soldesk6F.ondal.store.service;

import com.soldesk6F.ondal.store.entity.StoreDto;
import com.soldesk6F.ondal.store.entity.StoreImg;
import com.soldesk6F.ondal.store.entity.StoreRegisterDto;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.OwnerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final OwnerRepository ownerRepository;
    
	public void registerStore(StoreRegisterDto dto, User user) {
		String userId = user.getUserId();
		Owner owner = ownerRepository.findByUser_UserId(userId)
			.orElseThrow(() -> new IllegalStateException("해당 userId로 등록된 점주 정보가 없습니다."));

		MultipartFile file = dto.getStoreImgs();
		List<StoreImg> imgList = new ArrayList<>();

        if (file != null && !file.isEmpty()) {
        	try {
        		String uploadDir = "src/main/resources/static/img/store/";
				Path uploadPath = Paths.get(uploadDir);
				
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				
    			String uuidName = UUID.randomUUID() + "_" + file.getOriginalFilename();
    			Path filePath = Paths.get(uploadDir + uuidName);

    			Files.copy(file.getInputStream(), filePath);

    			StoreImg storeImg = StoreImg.builder()
    					.storeImg("/img/store/" + uuidName)
    					.store(null)
    					.build();

    			imgList.add(storeImg);

        	} catch (IOException e) {
        		throw new RuntimeException("이미지 저장 실패", e);
        	}
        }

        Store store = Store.builder()
			.owner(owner)
			.businessNum(dto.getBusinessNum())
			.storeName(dto.getStoreName())
			.category(dto.getCategory())
			.storePhone(dto.getStorePhone())
			.storeAddress(dto.getStoreAddress())
			.storeLatitude(dto.getLatitude())
			.storeLongitude(dto.getLongitude())
			.storeStatus(Store.StoreStatus.CLOSED)
			.storeImgs(imgList)
			.foodOrigin("")
			.build();
        for (StoreImg img : imgList) {
        	img.setStore(store);
        }
        storeRepository.save(store);
	}
    
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
//    	                    .imageUrl(store.getBrandImgs() != null && !store.getBrandImgs().isEmpty()
//    	                            ? store.getBrandImgs().get(0).getBrandImgFilePath() : "/img/default.png")
					.build();
				return dto;
			})
			.collect(Collectors.toList());
	}
}
