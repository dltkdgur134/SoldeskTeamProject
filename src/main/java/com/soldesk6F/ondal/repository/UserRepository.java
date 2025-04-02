package com.soldesk6F.ondal.repository;

import com.soldesk6F.ondal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email); // 이메일 중복 체크용
}