package com.soldesk6F.ondal.store.repository;

import com.soldesk6F.ondal.store.entity.StoreImg;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StoreImgRepository extends JpaRepository<StoreImg, UUID> {

}