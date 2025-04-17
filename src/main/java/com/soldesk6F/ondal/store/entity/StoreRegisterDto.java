package com.soldesk6F.ondal.store.entity;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRegisterDto {
    private String storeName;
    private String businessNum;
    private String storePhone;
    private String storeAddress;
    private String category;
    private double latitude;
    private double longitude;
    private MultipartFile storeImgs;
}