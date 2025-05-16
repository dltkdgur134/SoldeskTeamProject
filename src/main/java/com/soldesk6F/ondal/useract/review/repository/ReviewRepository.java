package com.soldesk6F.ondal.useract.review.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.review.entity.Review;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.User;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
	Optional<List<Review>> findAllByUser(User user);
	Optional<Review> findByOrder(Order order);
	
	long countByStore(Store store);

	@Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.store = :store")
	Double findAverageRatingByStore(@Param("store") Store store);
}
