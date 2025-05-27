// OrderRepository.java
package com.soldesk6F.ondal.useract.order.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToOwner;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToRider;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToUser;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByGuestId(String guestId);
    List<Order> findByStore_StoreId(UUID storeId);
	List<Order> findByUser(User user);
    List<Order> findAllByOrderToRider(OrderToRider pending);
    List<UUID> findIdByUser_UserUuidAndOrderToOwnerIn(
            UUID userUuid,
            List<OrderToOwner> orderToOwner);
    
    long countByStore(Store store);
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
    
 // 오늘 날짜 기준으로 가장 큰 orderNumber 반환
    @Query("SELECT MAX(o.orderNumber) FROM Order o WHERE o.orderTime >= :startOfDay AND o.orderTime < :endOfDay")
    Integer findMaxOrderNumberByDate(LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    /**
     *  특정 사용자(userUuid) && 주문 상태가 주어진 집합(statuses) 안에 포함되는
     *  주문의 ID(UUID) 만 뽑아 온다.
     */
//    @Query("""
//            select o.orderId
//            from Order o
//            where o.user.userUuid = :userUuid
//              and o.orderToOwner in :activeStatuses
//            """)
//        List<UUID> findActiveOrderIds(@Param("userUuid")       UUID userUuid,
//                                      @Param("activeStatuses") List<OrderToOwner> activeStatuses);
    
    @Query("""
    		select o.orderId
    		from Order o
    		where o.user.userUuid = :userUuid
    			and o.orderToUser in :activeStatuses
    		""")
    List<UUID> findActiveOrderIds(@Param("userUuid") UUID userUuid,
    		@Param("activeStatuses") List<OrderToUser> activeStatuses);
    
 // Owner 매출 관련 레포지토리

 // 1) ownerId로 기간 내 총 매출 합계 조회
    @Query("SELECT SUM(o.totalPrice) FROM Order o " +
           "WHERE o.store.owner.ownerId = :ownerId " +
           "AND o.orderTime BETWEEN :start AND :end")
    Integer sumSalesByOwnerIdAndPeriod(@Param("ownerId") UUID ownerId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    // 2) storeId로 기간 내 총 매출 합계 조회
    @Query("SELECT SUM(o.totalPrice) FROM Order o " +
           "WHERE o.store.storeId = :storeId " +
           "AND o.orderTime BETWEEN :start AND :end")
    Integer sumSalesByStoreIdAndPeriod(@Param("storeId") UUID storeId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    
    
}
