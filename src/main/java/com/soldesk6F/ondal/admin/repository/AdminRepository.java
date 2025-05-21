package com.soldesk6F.ondal.admin.repository;

import com.soldesk6F.ondal.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, String> {
	
}

