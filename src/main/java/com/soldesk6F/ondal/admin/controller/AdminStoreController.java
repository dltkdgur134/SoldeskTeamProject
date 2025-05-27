package com.soldesk6F.ondal.admin.controller;

import com.soldesk6F.ondal.store.entity.StoreApprovalDto;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.entity.Store.StoreStatus;
import com.soldesk6F.ondal.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// 관리자 매장 입점 신청 승인 페이지
@RestController
@RequestMapping("/api/admin/stores")
@RequiredArgsConstructor
public class AdminStoreController {

	private final StoreRepository storeRepository;

	@GetMapping("/pending")
	public List<StoreApprovalDto> getPendingStores() {
		return storeRepository.findByStoreStatus(StoreStatus.PENDING_APPROVAL).stream()
				.map(store -> StoreApprovalDto.builder()
						.storeId(store.getStoreId())
						.storeName(store.getStoreName())
						.businessNum(store.getBusinessNum())
						.storePhone(store.getStorePhone())
						.ownerId(store.getOwner().getUser().getUserId())
						.category(store.getCategory())
						.storeAddress(store.getStoreAddress())
						.brandImg(store.getBrandImg())
						.registrationDate(store.getRegistrationDate().toString())
						.build())
				.toList();
	}

	@PostMapping("/{id}/approve")
	public ResponseEntity<?> approveStore(@PathVariable("id") UUID id) {
		Store store = storeRepository.findById(id).orElseThrow();
		store.setStoreStatus(StoreStatus.CLOSED);
		storeRepository.save(store);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/reject")
	public ResponseEntity<?> rejectStore(@PathVariable("id") UUID id) {
		Store store = storeRepository.findById(id).orElseThrow();
		store.setStoreStatus(StoreStatus.PENDING_REFUSES);
		storeRepository.save(store);
		return ResponseEntity.ok().build();
	}

}
	

