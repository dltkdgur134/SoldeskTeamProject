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

import org.springframework.security.access.AccessDeniedException;
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
		log.info("DTO 값: {}", dto);
		log.info("사용자 정보: {}", user.getUserId());
		log.info("가게 이름: {}", dto.getStoreName());
		log.info("전화번호: {}", dto.getStorePhone());
		log.info("첨부파일: {}", dto.getBrandImg() != null ? dto.getBrandImg().getOriginalFilename() : "null");

		UUID userUuid = user.getUserUuid();
		Owner owner = ownerRepository.findByUser_UserUuid(userUuid)
			.orElseThrow(() -> new IllegalStateException("해당 아이디로 등록된 점주 정보가 없습니다."));

		String brandImgPath = null;

		MultipartFile file = dto.getBrandImg();
		if (file != null && !file.isEmpty()) {
			try {
				String originalName = file.getOriginalFilename();
				if (originalName == null || originalName.isBlank()) {
					throw new RuntimeException("파일 이름이 유효하지 않습니다.");
				}

				String uploadDir = "src/main/resources/static/img/store/";
				Path uploadPath = Paths.get(uploadDir);
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				String uuidName = UUID.randomUUID() + "_" + originalName;
				Path filePath = Paths.get(uploadDir + uuidName);
				Files.copy(file.getInputStream(), filePath);

				brandImgPath = "/img/store/" + uuidName;
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
			.brandImg(brandImgPath) // ✅ 이 부분만 이미지 저장
			.foodOrigin("")
			.build();

		storeRepository.save(store);
	}
    
	public List<StoreDto> getStoresByCategory(String category) {
    	
		log.info("카테고리로 가게 검색 요청: {}", category);
		
		List<Store> stores = storeRepository.findByCategory(category);
		
		log.info("조회된 가게 수: {}", stores.size());
		
		return storeRepository.findByCategory(category).stream()
			.map(store -> {
				String imageUrl = (store.getBrandImg() != null && !store.getBrandImg().isBlank())
						? store.getBrandImg() : "/img/store/default.png";
				StoreDto dto = StoreDto.builder()
					.storeName(store.getStoreName())
					.category(store.getCategory())
					.storePhone(store.getStorePhone())
					.storeAddress(store.getStoreAddress())
					.storeIntroduce(store.getStoreIntroduce())
					.storeStatus(store.getStoreStatus().getDescription())
    	            .imageUrl(imageUrl)
					.build();
				return dto;
			})
			.collect(Collectors.toList());
	}
	
	public List<Store> findStoresByOwner(Owner owner) {
		List<Store> stores = storeRepository.findByOwner(owner);
		System.out.println("📦 StoreRepository에서 조회된 가게 수: " + stores.size());
		return stores;
	}
	
	public Store getStoreForOwner(UUID storeId, String authenticatedUserId) {
	    Store store = storeRepository.findById(storeId)
	        .orElseThrow(() -> new IllegalArgumentException("점포를 찾을 수 없습니다."));

	    if (!store.getOwner().getUser().getUserId().equals(authenticatedUserId)) {
	        throw new AccessDeniedException("접근 권한이 없습니다.");
	    }

	    return store;
	}
	
	public Store findById(UUID storeId) {
		return storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 매장을 찾을 수 없습니다."));
	}
	
}
