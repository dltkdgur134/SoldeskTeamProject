package com.soldesk6F.ondal.store.service;

import com.soldesk6F.ondal.store.entity.StoreDto;
import com.soldesk6F.ondal.store.entity.StoreImg;
import com.soldesk6F.ondal.store.entity.StoreIntroduceImg;
import com.soldesk6F.ondal.store.entity.StoreRegisterDto;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.repository.StoreImgRepository;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.OwnerRepository;
import com.soldesk6F.ondal.useract.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
import java.time.LocalTime;
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
	private final StoreImgRepository storeImgRepository;
	private final ReviewRepository reviewRepository;

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
				.foodOrigin("").build();

		storeRepository.save(store);
	}

	public List<StoreDto> getStoresByCategory(String category) {

		log.info("카테고리로 가게 검색 요청: {}", category);

		List<Store> stores = storeRepository.findByCategory(category);

		log.info("조회된 가게 수: {}", stores.size());

		return storeRepository.findByCategory(category).stream().map(store -> {
			String imageUrl = (store.getBrandImg() != null && !store.getBrandImg().isBlank()) ? store.getBrandImg()
					: "/img/store/default.png";
			double avgRating = reviewRepository.findAverageRatingByStore(store);
			long reviewCount = reviewRepository.countByStore(store);
			StoreDto dto = StoreDto.builder().storeId(store.getStoreId()).storeName(store.getStoreName())
					.category(store.getCategory()).storePhone(store.getStorePhone())
					.storeAddress(store.getStoreAddress()).storeIntroduce(store.getStoreIntroduce())
					.storeStatus(store.getStoreStatus().name()).imageUrl(imageUrl)
					.avgRating(avgRating).reviewCount(reviewCount).build();
			return dto;
		}).collect(Collectors.toList());
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
			String storeAddress, String category, String storeStatus, String storeIntroduce, LocalTime openingTime,
			LocalTime closingTime, String holiday, Store.DeliveryRange deliveryRange, String storeEvent,
			String foodOrigin, Double latitude, Double longitude) {

		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

		if (!store.getOwner().getUser().getUserId().equals(loginUserId)) {
			throw new AccessDeniedException("본인의 점포만 수정할 수 있습니다.");
		}

		store.setStoreName(storeName);
		store.setStorePhone(storePhone);
		store.setStoreAddress(storeAddress);
		store.setCategory(category);
		store.setStoreStatus(Store.StoreStatus.valueOf(storeStatus));
		store.setStoreIntroduce(storeIntroduce);
		store.setOpeningTime(openingTime);
		store.setClosingTime(closingTime);
		store.setDeliveryRange(deliveryRange);
		store.setHoliday(holiday);
		store.setStoreEvent(storeEvent);
		store.setFoodOrigin(foodOrigin);
		store.setStoreLatitude(latitude);
		store.setStoreLongitude(longitude);

		storeRepository.save(store);
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
}
