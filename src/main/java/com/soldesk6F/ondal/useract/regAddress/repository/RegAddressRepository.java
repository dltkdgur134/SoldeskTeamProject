package com.soldesk6F.ondal.useract.regAddress.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.regAddress.entity.RegAddress;

@Repository
public interface RegAddressRepository extends JpaRepository<RegAddress, UUID> {
	Optional<List<RegAddress>> findAllByUser(User user);
	Optional<RegAddress> findByUser(User user);
	Optional<RegAddress> findByRegAddressIdAndUser(UUID regAddressId, User user);
	Optional<RegAddress> findByUserAndIsUserSelectedAddressTrue(User user);
}
