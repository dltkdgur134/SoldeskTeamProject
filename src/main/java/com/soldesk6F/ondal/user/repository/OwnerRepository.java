package com.soldesk6F.ondal.user.repository;

import com.soldesk6F.ondal.user.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, UUID> {

    // userId를 통해 Owner를 찾고 싶을 때
    Optional<Owner> findByUser_UserId(String userId);

    // user의 id가 특정 값인지 확인
    boolean existsByUser_UserId(String userId);
}
