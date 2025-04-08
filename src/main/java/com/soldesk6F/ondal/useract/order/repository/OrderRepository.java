// OrderRepository.java
package com.soldesk6F.ondal.useract.order.repository;

import com.soldesk6F.ondal.useract.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByGuestId(String guestId);
}
