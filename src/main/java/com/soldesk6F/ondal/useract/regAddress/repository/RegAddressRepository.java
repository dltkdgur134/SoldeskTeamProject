package com.soldesk6F.ondal.useract.regAddress.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.regAddress.entity.RegAddress;

public interface RegAddressRepository extends JpaRepository<RegAddress, UUID> {
	Optional<List<RegAddress>> findAllByUser(User user);
	Optional<RegAddress> findByUser(User user);
}
