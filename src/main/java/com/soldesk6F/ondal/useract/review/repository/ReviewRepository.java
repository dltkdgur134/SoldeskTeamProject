package com.soldesk6F.ondal.useract.review.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.review.entity.Review;
import com.soldesk6F.ondal.user.entity.User;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
	Optional<List<Review>> findAllByUser(User user);
	Optional<Review> findByOrder(Order order);
}
