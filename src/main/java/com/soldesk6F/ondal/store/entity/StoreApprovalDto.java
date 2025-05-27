package com.soldesk6F.ondal.store.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class StoreApprovalDto {
	private UUID storeId;
	private String storeName;
	private String businessNum;
	private String storePhone;
	private String ownerId;
	private String category;
	private String storeAddress;
	private String brandImg;
	private String registrationDate;
}


