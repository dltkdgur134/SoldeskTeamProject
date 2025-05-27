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
	List<Store> findByOwnerOwnerId(UUID ownerId);

    @Query(
            value = """
            SELECT  s.*,
                    ST_Distance_Sphere(
                        s.location,
                        ST_GeomFromText(:point)        -- ▶️ 4326 SRID 쓰는 경우 ST_SRID(...,4326) 추가
                    ) AS distance
            FROM    store s
            WHERE   MBRContains( ST_GeomFromText(:polygon), s.location )
              AND  ( s.name LIKE CONCAT('%', :keyword, '%')
                     OR s.name IN (:recommended) )
            HAVING  distance <= :radius            -- 별칭 사용 가능
            ORDER BY distance
            """,
            /* 페이징용 count 쿼리 */
            countQuery = """
            SELECT  COUNT(*)
            FROM    store s
            WHERE   MBRContains( ST_GeomFromText(:polygon), s.location )
              AND  ( s.name LIKE CONCAT('%', :keyword, '%')
                     OR s.name IN (:recommended) )
            """
            , nativeQuery = true)
        Page<Store> searchNearby(
                @Param("point")      String pointWKT,       // "POINT(127.001 37.567)"
                @Param("polygon")    String polygonWKT,     // "POLYGON((x1 y1, ...))"
                @Param("keyword")    String keyword,
                @Param("recommended") Collection<String> recommended,
                @Param("radius")     int radiusMeters,
                Pageable pageable                        // LIMIT/OFFSET 자동 주입
        );
	}


