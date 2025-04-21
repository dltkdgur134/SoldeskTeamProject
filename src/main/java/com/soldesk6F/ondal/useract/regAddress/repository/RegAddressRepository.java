package com.soldesk6F.ondal.useract.regAddress.repository;

import java.awt.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.regAddress.entity.RegAddress;

public interface RegAddressRepository extends JpaRepository<RegAddress, UUID> {
	
}
