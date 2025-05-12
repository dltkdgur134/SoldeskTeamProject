package com.soldesk6F.ondal.rider.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soldesk6F.ondal.rider.entity.DeliverySales;
import com.soldesk6F.ondal.rider.entity.DeliverySales.DeliveryStatus;
import com.soldesk6F.ondal.user.entity.Rider;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliverySalesRepository extends JpaRepository<DeliverySales, UUID> {

	List<DeliverySales> findByRiderManagement_Rider(Rider rider);

	// 라이더의 월별 매출 조회
	@Query(value = """
			        SELECT SUM(ds.deliveryPrice)
			        FROM DeliverySales ds
			        WHERE ds.riderManagement.rider = :rider
			        AND FUNCTION('YEAR', ds.deliverySalesDate) = :year
			        AND FUNCTION('MONTH', ds.deliverySalesDate) = :month
			""", nativeQuery = false)
	BigDecimal findMonthlySalesByRider(@Param("rider") Rider rider, @Param("year") int year, @Param("month") int month);

	// 해당 월에 완료한 배달 수 조회
	@Query("""
		    SELECT COUNT(ds)
		    FROM DeliverySales ds
		    WHERE ds.riderManagement.rider = :rider
		    AND FUNCTION('YEAR', ds.deliverySalesDate) = :year
		    AND FUNCTION('MONTH', ds.deliverySalesDate) = :month
		    AND ds.deliveryStatus = :status
		""")
		long countOrdersByRider(@Param("rider") Rider rider, @Param("year") int year, 
		                        @Param("month") int month, @Param("status") DeliveryStatus status);

	@Query("""
		    SELECT 
		        rm.rider.riderId, 
		        SUM(ds.deliveryPrice), 
		        COUNT(ds)
		    FROM 
		        DeliverySales ds
		    JOIN ds.riderManagement rm
		    WHERE 
		        FUNCTION('YEAR', ds.deliverySalesDate) = :year
		        AND FUNCTION('MONTH', ds.deliverySalesDate) = :month
		        AND ds.deliveryStatus = 'COMPLETED'
		    GROUP BY 
		        rm.rider.riderId
		""")
		List<Object[]> findAllRiderMonthlySalesAndCount(@Param("year") int year, @Param("month") int month);
}
