package com.soldesk6F.ondal.store.repository;

import com.soldesk6F.ondal.store.dto.StoreDistanceProjection;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.Owner;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
	List<Store> findByCategory(String category);
	List<Store> findByOwner(Owner owner);
	 
	 
	 
	 @Query(value = """
			 SELECT  s.id   AS id,
			         s.name AS name,
			         ST_Distance_Sphere(
			             POINT(:userLon, :userLat),
			             s.location
			         ) AS distance_meters
			 FROM    store AS s
			 WHERE   ST_X(s.location) BETWEEN (:userLon - (:radiusKm / (111.0 * COS(RADIANS(:userLat)))))
			                          AND     (:userLon + (:radiusKm / (111.0 * COS(RADIANS(:userLat)))))
			   AND   ST_Y(s.location) BETWEEN (:userLat - (:radiusKm / 111.0))
			                          AND     (:userLat + (:radiusKm / 111.0))
			 HAVING  distance_meters <= (:radiusKm * 1000)
			 ORDER BY distance_meters
			 """, nativeQuery = true)
			 List<StoreDistanceProjection> findStoresWithinRadius(
			         @Param("userLat")  double userLat,
			         @Param("userLon")  double userLon,
			         @Param("radiusKm") double radiusKm,
			         @Param("keyword") String keyword
			 );
	 
	 
	 
	boolean existsByOwner_OwnerId(UUID ownerId);
	Store findByStoreId(UUID StoreId);
	List<Store> findAll();
	@Query("SELECT s FROM Store s LEFT JOIN FETCH s.storeImgs WHERE s.storeId = :storeId")
	Optional<Store> findWithStoreImgsByStoreId(@Param("storeId") UUID storeId);
}






