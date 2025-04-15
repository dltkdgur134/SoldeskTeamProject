package com.soldesk6F.ondal.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


import com.soldesk6F.ondal.user.entity.User;


public interface UserRepository extends JpaRepository<User,UUID> {
	boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
    boolean existsByUserPhone(String userPhone);
    boolean existsByNickName(String nickName);
    Optional<User> findByEmail(String email);  		
    Optional<User> findBySocialLoginProvider(String provider);
    long deleteBySocialLoginProvider(String provider);
    Optional<User> findByUserId(String userId);
    Optional<User> findByUserUuid(UUID userUuid);
    Optional<User> findByNickName(String nickName);
    Optional<User> findByUserPhone(String userPhone);
}