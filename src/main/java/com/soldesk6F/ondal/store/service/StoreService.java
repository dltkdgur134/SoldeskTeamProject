package com.soldesk6F.ondal.store.service;

import com.soldesk6F.ondal.store.entity.StoreDto;
import com.soldesk6F.ondal.store.entity.StoreImg;
import com.soldesk6F.ondal.store.entity.StoreIntroduceImg;
import com.soldesk6F.ondal.store.entity.StoreRegisterDto;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.entity.Store.StoreStatus;
import com.soldesk6F.ondal.store.repository.StoreImgRepository;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.OwnerRepository;
import com.soldesk6F.ondal.useract.regAddress.entity.RegAddress;
import com.soldesk6F.ondal.useract.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import static java.lang.Math.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final OwnerRepository ownerRepository;
	private final StoreImgRepository storeImgRepository;
	private final ReviewRepository reviewRepository;

	public void registerStore(StoreRegisterDto dto, User user) {
		log.info("DTO 값: {}", dto);
		log.info("사용자 정보: {}", user.getUserId());
		log.info("가게 이름: {}", dto.getStoreName());
		log.info("전화번호: {}", dto.getStorePhone());
		log.info("첨부파일: {}", dto.getBrandImg() != null ? dto.getBrandImg().getOriginalFilename() : "null");
		log.info("배달료 : {}",dto.getDeliveryFee());
		UUID userUuid = user.getUserUuid();
		Owner owner = ownerRepository.findByUser_UserUuid(userUuid)
				.orElseThrow(() -> new IllegalStateException("해당 아이디로 등록된 점주 정보가 없습니다."));

		String brandImgPath = null;
		
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
		Point location = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));

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
				.owner(owner).businessNum(dto.getBusinessNum()).storeName(dto.getStoreName())
				.category(dto.getCategory()).storePhone(dto.getStorePhone()).storeAddress(dto.getStoreAddress())
				.storeLatitude(dto.getLatitude()).storeLongitude(dto.getLongitude()).storeLocation(location)
				.storeStatus(Store.StoreStatus.PENDING_APPROVAL).brandImg(brandImgPath)
				.foodOrigin("").deliveryFee(dto.getDeliveryFee()).lastOrderDate(LocalDateTime.now())
				.build();

		storeRepository.save(store);
	}

	/*
	 * public List<StoreDto> getStoresByCategory(String category, User user) {
	 * RegAddress userAddress = user.getUserSelectedAddress(); double userLat =
	 * userAddress.getUserAddressLatitude(); double userLon =
	 * userAddress.getUserAddressLongitude();
	 * 
	 * log.info("카테고리로 가게 검색 요청: {}", category);
	 * 
	 * List<Store> stores = storeRepository.findByCategory(category);
	 * 
	 * log.info("조회된 가게 수: {}", stores.size());
	 * 
	 * return stores.stream().map(store -> { // 가게 이미지 String imageUrl =
	 * (store.getBrandImg() != null && !store.getBrandImg().isBlank()) ?
	 * store.getBrandImg() : "/img/store/default.png"; // 가게 평점 계산 double avgRating
	 * = reviewRepository.findAverageRatingByStore(store); // 리뷰 long reviewCount =
	 * reviewRepository.countByStore(store); // 거리 계산 double distance =
	 * calculateDistance(userLat, userLon, store.getStoreLatitude(),
	 * store.getStoreLongitude()); StoreDto dto = StoreDto.builder()
	 * .storeId(store.getStoreId()) .storeName(store.getStoreName())
	 * .category(store.getCategory()) .storePhone(store.getStorePhone())
	 * .storeAddress(store.getStoreAddress())
	 * .storeIntroduce(store.getStoreIntroduce())
	 * .storeStatus(store.getStoreStatus().name()) .imageUrl(imageUrl)
	 * .avgRating(avgRating) .reviewCount(reviewCount) .distanceInKm(distance)
	 * .build(); return dto; }).collect(Collectors.toList()); }
	 */
	
	public List<StoreDto> getStoresByCategory(String category, User user) {
		// 로그인된 유저의 선택된 주소에서 위도/경도 추출
		RegAddress userAddress = user.getUserSelectedAddress();
		double userLat = userAddress.getUserAddressLatitude();   // 사용자 위도
		double userLon = userAddress.getUserAddressLongitude();  // 사용자 경도

		// 거리 필터링 기준 반경 (km)
		double radiusKm = 6.0;

		// 전체 가게 조회
		List<Store> allStores = storeRepository.findAll();

		// 서비스에서 직접 거리 계산 + 범위 + 카테고리 필터
		return allStores.stream()
			//  각 가게에 대해 사용자와의 거리를 계산하여 Map.Entry<Store, Distance> 형태로 반환
			.map(store -> { 
				// 거리 계산
				double distance = calculateDistance(
					userLat, userLon,
					store.getStoreLatitude(), store.getStoreLongitude()
				);
				return new AbstractMap.SimpleEntry<>(store, distance);
			})
			// 6km 반경 필터
			.filter(entry -> entry.getValue() <= radiusKm)
			// 카테고리 필터
			.filter(entry -> category.trim().equals(entry.getKey().getCategory().trim()))
			// StoreDto로 변환
			.map(entry -> {
				Store store = entry.getKey();
				double distance = entry.getValue();

				String imageUrl = (store.getBrandImg() != null && !store.getBrandImg().isBlank())
					? store.getBrandImg()
					: "/img/store/default.png"; // 이미지 없으면 default.png로 대체

				return StoreDto.builder()
					.storeId(store.getStoreId())
					.storeName(store.getStoreName())
					.category(store.getCategory())
					.storePhone(store.getStorePhone())
					.storeAddress(store.getStoreAddress())
					.storeIntroduce(store.getStoreIntroduce())
					.storeStatus(store.getStoreStatus().name())
					.imageUrl(imageUrl)
					.avgRating(reviewRepository.findAverageRatingByStore(store))
					.reviewCount(reviewRepository.countByStore(store))
					.distanceInKm(Math.round(distance * 10) / 10.0)
					.build();
			})
			.toList();
	}


	public List<Store> findStoresByOwner(Owner owner) {
		List<Store> stores = storeRepository.findByOwner(owner);
		System.out.println("📦 StoreRepository에서 조회된 가게 수: " + stores.size());
		return stores;
	}

	public Store getStoreForOwner(UUID storeId, String authenticatedUserId) {
		Store store = storeRepository.findWithStoreImgsByStoreId(storeId)
				.orElseThrow(() -> new IllegalArgumentException("점포를 찾을 수 없습니다."));

		if (!store.getOwner().getUser().getUserId().equals(authenticatedUserId)) {
			throw new AccessDeniedException("접근 권한이 없습니다.");
		}

		return store;
	}

	public Store findStoreByStoreId(UUID storeId) {
		return storeRepository.findByStoreId(storeId);
	}

	public Store findById(UUID storeId) {
		return storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당 매장을 찾을 수 없습니다."));
	}

	public List<Store> findAll() {
		return storeRepository.findAll();
	}

	@Transactional
	public void updateStoreInfo(UUID storeId, String loginUserId, String storeName, String storePhone,
			String storeAddress, String category, StoreStatus storeStatus, String storeIntroduce, LocalTime openingTime,
			LocalTime closingTime, String holiday, Store.DeliveryRange deliveryRange, String storeEvent,
			String foodOrigin, Double latitude, Double longitude,int deliveryFee) {

		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));
		
		
		
		if (!store.getOwner().getUser().getUserId().equals(loginUserId)) {
			throw new AccessDeniedException("본인의 점포만 수정할 수 있습니다.");
		}

		store.setStoreName(storeName);
		store.setStorePhone(storePhone);
		store.setStoreAddress(storeAddress);
		store.setCategory(category);
		store.setStoreStatus(storeStatus);
		store.setStoreIntroduce(storeIntroduce);
		store.setOpeningTime(openingTime);
		store.setClosingTime(closingTime);
		store.setDeliveryRange(deliveryRange);
		store.setHoliday(holiday);
		store.setStoreEvent(storeEvent);
		store.setFoodOrigin(foodOrigin);
		store.setStoreLatitude(latitude);
		store.setStoreLongitude(longitude);
		store.setDeliveryFee(deliveryFee);

		storeRepository.save(store);
		storeRepository.flush();
	}

	public void uploadBrandImg(UUID storeId, String loginUserId, MultipartFile brandImgFile) throws IOException {
		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

		if (!store.getOwner().getUser().getUserId().equals(loginUserId)) {
			throw new AccessDeniedException("본인의 점포만 수정할 수 있습니다.");
		}

		if (brandImgFile != null && !brandImgFile.isEmpty()) {

			final long MAX_FILE_SIZE = 3 * 1024 * 1024;
			if (brandImgFile.getSize() > MAX_FILE_SIZE) {
				throw new IllegalArgumentException("파일 크기는 3MB 이하로 해주세요.");
			}

			String originalFilename = Paths.get(brandImgFile.getOriginalFilename()).getFileName().toString();
			String lowerCaseFilename = originalFilename.toLowerCase();
			if (!(lowerCaseFilename.endsWith(".jpg") || lowerCaseFilename.endsWith(".jpeg")
					|| lowerCaseFilename.endsWith(".png") || lowerCaseFilename.endsWith(".bmp"))) {
				throw new IllegalArgumentException("jpg, jpeg, png, bmp 확장자만 업로드할 수 있습니다.");
			}

			String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/img/store/";
			Files.createDirectories(Paths.get(uploadDir));

			if (store.getBrandImg() != null && !store.getBrandImg().isBlank()) {
				String relativePath = store.getBrandImg();
				if (relativePath.startsWith("/")) {
					relativePath = relativePath.substring(1);
				}
				Path existingFilePath = Paths.get(System.getProperty("user.dir"), "src/main/resources/static",
						relativePath);
				try {
					Files.deleteIfExists(existingFilePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			String newFilename = UUID.randomUUID() + "_" + originalFilename;
			Path filePath = Paths.get(uploadDir, newFilename);
			Files.write(filePath, brandImgFile.getBytes());

			store.setBrandImg("/img/store/" + newFilename);
			storeRepository.save(store);
		}
	}

	public void uploadStoreImgs(UUID storeId, String loginUserId, MultipartFile[] storeImgFiles) throws Exception {
		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new IllegalArgumentException("해당 점포를 찾을 수 없습니다."));

		if (!store.getOwner().getUser().getUserId().equals(loginUserId)) {
			throw new AccessDeniedException("본인의 점포만 수정할 수 있습니다.");
		}

		String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/img/store/storeimg/";
		Files.createDirectories(Paths.get(uploadDir));

		for (MultipartFile file : storeImgFiles) {
			if (!file.isEmpty()) {
				String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
				Path filePath = Paths.get(uploadDir, filename);
				Files.write(filePath, file.getBytes());

				StoreImg storeImg = new StoreImg();
				storeImg.setStore(store);
				storeImg.setStoreImg("/img/store/storeimg/" + filename);

				storeImgRepository.save(storeImg);
			}
		}

		storeRepository.save(store);
	}
	
	@Transactional
	public void updateStoreImg(UUID storeId, String userId, UUID storeImgId, MultipartFile newImgFile) throws IOException {
		Store store = getStoreForOwner(storeId, userId);

		StoreImg target = store.getStoreImgs().stream()
			.filter(i -> i.getStoreImgId().equals(storeImgId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("소개 이미지를 찾을 수 없습니다."));

		// 기존 이미지 삭제
		Path oldPath = Paths.get(target.getStoreImg().replaceFirst("/img", "src/main/resources/static/img"));
		Files.deleteIfExists(oldPath);

		// 새 이미지 저장
		String savedPath = saveImageFile(newImgFile, "store-introduce");
		target.setStoreImg(savedPath);
	}
	
	@Transactional
	public void deleteStoreImg(UUID storeId, String userId, UUID storeImgId) throws IOException {
		Store store = getStoreForOwner(storeId, userId);

		StoreImg target = store.getStoreImgs().stream()
			.filter(i -> i.getStoreImgId().equals(storeImgId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("소개 이미지를 찾을 수 없습니다."));

		// 실제 파일 삭제
		Path imgPath = Paths.get(target.getStoreImg().replaceFirst("/img", "src/main/resources/static/img"));
		Files.deleteIfExists(imgPath);

		// 엔티티 제거
		store.getStoreImgs().remove(target);
		storeImgRepository.delete(target);
	}
	
	private String saveImageFile(MultipartFile file, String subFolder) throws IOException {
		String baseDir = new File("").getAbsolutePath();  // 프로젝트 루트 경로

		// 저장 경로 설정 (절대경로로)
		Path uploadPath = Paths.get(baseDir, "src", "main", "resources", "static", "img", subFolder);
		if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

		// 저장 파일명 구성
		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null) throw new IllegalArgumentException("파일 이름이 없습니다.");

		String ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
		List<String> allowedExtensions = List.of(".jpg", ".jpeg", ".png", ".gif");
		if (!allowedExtensions.contains(ext)) throw new IllegalArgumentException("지원하지 않는 확장자입니다: " + ext);

		String filename = UUID.randomUUID() + ext;

		// 파일 저장
		Path fullPath = uploadPath.resolve(filename);
		file.transferTo(fullPath.toFile());

		// 반환값은 웹 경로
		return "/img/" + subFolder + "/" + filename;
	}
	
	@Transactional(readOnly = true)
	public Store findByIdWithImgs(UUID storeId) {
		return storeRepository.findWithStoreImgsByStoreId(storeId)
			.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));
	}
	
	// 로그인된 유저 주소를 기준으로 가게 주소와 거리 계산 (호의 길이 계산 공식)
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
