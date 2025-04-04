package com.soldesk6F.ondal.repository;

import com.soldesk6F.ondal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

	boolean existsById(String userId); // 아이디 중복 체크용
    boolean existsByEmail(String email); // 이메일 중복 체크용
    
}