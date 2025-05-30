package com.soldesk6F.ondal.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RiderRepository extends JpaRepository<Rider, UUID> {
	
	boolean existsByUser_UserId(String userId);
	Optional<Rider> findByUser_UserId(String userId);
	Optional<Rider> findByUser_UserUuid(UUID userUuid);
	void deleteByUser_UserUuid(UUID userUuid);
	Optional<Rider> findByUser(User user);
	@Query(value = """
		    SELECT r.rider_id, SUM(ds.delivery_price) AS total_sales, COUNT(ds.delivery_sales_id) AS total_deliveries
		    FROM rider r
		    JOIN rider_management rm ON rm.rider_id = r.rider_id
		    JOIN delivery_sales ds ON ds.rider_management_id = rm.rider_management_id
		    WHERE (
		        6371 * acos(
		            cos(radians(:lat)) * cos(radians(r.hub_address_latitude)) *
		            cos(radians(r.hub_address_longitude) - radians(:lng)) +
		            sin(radians(:lat)) * sin(radians(r.hub_address_latitude))
		        )
		    ) <= :radius
		    AND FUNCTION('MONTH', ds.delivery_sales_date) = :month
		    AND FUNCTION('YEAR', ds.delivery_sales_date) = :year
		    GROUP BY r.rider_id
		""", nativeQuery = true)
		List<Object[]> findNearbyRidersMonthlySales(@Param("lat") double lat,
		                                            @Param("lng") double lng,
		                                            @Param("radius") double radius,
		                                            @Param("month") int month,
		                                            @Param("year") int year);
}
