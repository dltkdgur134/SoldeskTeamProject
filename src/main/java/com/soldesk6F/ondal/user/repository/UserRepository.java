package com.soldesk6F.ondal.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.soldesk6F.ondal.user.entity.User;


public interface UserRepository extends JpaRepository<User, String> {
	boolean existsById(String userId);
    boolean existsByEmail(String email);
    boolean existsByUserPhone(String userPhone);
    Optional<User> findByEmail(String email);  		
    Optional<User> findByUserId(String userId);    
    Optional<User> findBySocialLoginProvider(String provider);
    long deleteBySocialLoginProvider(String provider);
}