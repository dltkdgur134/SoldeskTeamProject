// OrderRepository.java
package com.soldesk6F.ondal.useract.order.repository;

import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToRider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByGuestId(String guestId);
    List<Order> findByStore_StoreId(UUID storeId);
    List<Order> findAllByOrderToRider(OrderToRider pending);
    
 // 반경 내 주문 조회 쿼리 추가
    @Query(value = """
    	    SELECT o.* 
    	    FROM orders o 
    	    JOIN store s ON o.store_id = s.store_id 
    	    WHERE (
    	        6371 * acos(
    	            cos(radians(:lat)) * cos(radians(s.store_latitude)) *
    	            cos(radians(s.store_longitude) - radians(:lng)) +
    	            sin(radians(:lat)) * sin(radians(s.store_latitude))
    	        )
    	    ) <= :radius
    	    AND o.order_to_rider IN ('PENDING', 'CONFIRMED')
    	""", nativeQuery = true)
    	List<Order> findOrdersWithinRadius(@Param("lat") double lat,
    	                                   @Param("lng") double lng,
    	                                   @Param("radius") double radiusKm);
}
