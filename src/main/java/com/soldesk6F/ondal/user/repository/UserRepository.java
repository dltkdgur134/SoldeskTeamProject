package com.soldesk6F.ondal.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.soldesk6F.ondal.user.entity.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User,UUID>, JpaSpecificationExecutor<User> {
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
    void deleteByUserId(String userId);
    
    @Modifying
	@Transactional
	@Query("UPDATE User u SET u.ondalWallet = u.ondalWallet + :amount WHERE u.userUuid = :userUuid")
	void addOndalWallet(@Param("userUuid") UUID userUuid, @Param("amount") int amount);
}