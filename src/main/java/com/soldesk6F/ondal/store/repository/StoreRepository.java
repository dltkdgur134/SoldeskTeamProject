package com.soldesk6F.ondal.store.repository;

import com.soldesk6F.ondal.store.dto.StoreDistanceProjection;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.entity.Store.StoreStatus;
import com.soldesk6F.ondal.user.entity.Owner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
	List<Store> findByCategory(String category);
	List<Store> findByOwner(Owner owner);
	List<Store> findByStoreStatus(StoreStatus status);
	 
	 
//	 @Query(value = """
//			 SELECT  s.id   AS id,
//			         s.name AS name,
//			         ST_Distance_Sphere(
//			             POINT(:userLon, :userLat),
//			             s.location
//			         ) AS distance_meters
//			 FROM    store AS s
//			 WHERE   ST_X(s.location) BETWEEN (:userLon - (:radiusKm / (111.0 * COS(RADIANS(:userLat)))))
//			                          AND     (:userLon + (:radiusKm / (111.0 * COS(RADIANS(:userLat)))))
//			   AND   ST_Y(s.location) BETWEEN (:userLat - (:radiusKm / 111.0))
//			                          AND     (:userLat + (:radiusKm / 111.0))
//			 HAVING  distance_meters <= (:radiusKm * 1000)
//			 ORDER BY distance_meters
//			 """, nativeQuery = true)
//			 List<StoreDistanceProjection> findStoresWithinRadius(
//			         @Param("userLat")  double userLat,
//			         @Param("userLon")  double userLon,
//			         @Param("radiusKm") double radiusKm,
//			         @Param("keyword") String keyword
//			 );
	boolean existsByOwner_OwnerId(UUID ownerId);
	Store findByStoreId(UUID StoreId);
	List<Store> findAll();
	@Query("SELECT s FROM Store s LEFT JOIN FETCH s.storeImgs WHERE s.storeId = :storeId")
	Optional<Store> findWithStoreImgsByStoreId(@Param("storeId") UUID storeId);
	List<Store> findByOwnerOwnerId(UUID ownerId);

	@Query(
		    value = """
		        SELECT  s.*,
		                ST_Distance_Sphere(
		                    s.store_location,
		                    ST_SRID(Point(:lon, :lat), 4326)
		                ) AS dist
		        FROM    store s
		        WHERE   MBRContains(
		                    ST_GeomFromText(:bbox, 4326),   
		                    s.store_location
		                )
		          AND   ST_Distance_Sphere(
		                    s.store_location,
		                    ST_SRID(Point(:lon, :lat), 4326)
		                ) <= :radius
		          AND  (
		                    s.store_name LIKE CONCAT('%', :original, '%')
		                    OR s.store_name LIKE CONCAT('%', :bestMatcher, '%')
		                    OR EXISTS (
		                           SELECT 1
		                           FROM   menu m
		                           WHERE  m.store_id = s.store_id
		                             AND (
		                                  m.menu_name LIKE CONCAT('%', :original, '%')
		                                  OR m.menu_name LIKE CONCAT('%', :bestMatcher, '%')
		                                 )
		                       )
		               )
		        ORDER BY dist ASC
		        """,
		    countQuery = """
		        SELECT COUNT(*)
		        FROM   store s
		        WHERE  MBRContains(
		                   ST_GeomFromText(:bbox, 4326),
		                   s.store_location
		               )
		          AND  ST_Distance_Sphere(
		                   s.store_location,
		                   ST_SRID(Point(:lon, :lat), 4326)
		               ) <= :radius
		          AND  (
		                   s.store_name LIKE CONCAT('%', :original, '%')
		                   OR s.store_name LIKE CONCAT('%', :bestMatcher, '%')
		                   OR EXISTS (
		                          SELECT 1
		                          FROM   menu m
		                          WHERE  m.store_id = s.store_id
		                            AND (
		                                 m.menu_name LIKE CONCAT('%', :original, '%')
		                                 OR m.menu_name LIKE CONCAT('%', :bestMatcher, '%')
		                                )
		                      )
		               )
		        """,
		    nativeQuery = true
		)
		Page<Store> searchNearbyStoresByDistance(
		        @Param("lon")        double lon,
		        @Param("lat")        double lat,
		        @Param("bbox")       String bbox,          // ← WKT 문자열
		        @Param("radius")     int    radiusMeters,
		        @Param("original")   String original,
		        @Param("bestMatcher")String bestMatcher,
		        Pageable pageable
		);

	

}


