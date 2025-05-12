package com.soldesk6F.ondal.rider.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.rider.entity.DeliverySales.DeliveryStatus;
import com.soldesk6F.ondal.rider.repository.DeliverySalesRepository;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.repository.RiderRepository;

@Service
public class RiderSalesService {

    @Autowired
    private DeliverySalesRepository deliverySalesRepository;
    @Autowired
    private RiderRepository riderRepository;

    // 라이더의 월별 평균 매출 조회
    public BigDecimal getMonthlyAverageSales(CustomUserDetails userDetails, int year, int month) {
        // 1. CustomUserDetails에서 riderId를 직접 가져옴
        String userId = userDetails.getUser().getUserId();
        Rider rider = riderRepository.findByUser_UserId(userId).orElseThrow();

        // 2. 라이더의 해당 월 매출 합계 조회
        BigDecimal totalSales = deliverySalesRepository.findMonthlySalesByRider(rider, year, month);

        // 3. 라이더가 해당 월에 완료한 배달 수 조회 (매출이 발생한 주문의 수)
        long completedOrderCount = deliverySalesRepository.countOrdersByRider(rider, year, month, DeliveryStatus.COMPLETED);

        // 4. 매출이 0이 아니면 평균 매출 계산, 아니면 0
        if (totalSales != null && completedOrderCount > 0) {
            return totalSales.divide(BigDecimal.valueOf(completedOrderCount), RoundingMode.HALF_UP); // 평균 매출 계산
        }

        return BigDecimal.ZERO; // 배달이 없거나 매출이 0인 경우
    }
 // 라이더의 월 배달 횟수 조회
    public long getCompletedOrderCount(CustomUserDetails userDetails, int year, int month) {
        // 1. CustomUserDetails에서 riderId를 직접 가져옴
        String userId = userDetails.getUser().getUserId();
        Rider rider = riderRepository.findByUser_UserId(userId).orElseThrow();

        // 2. 라이더의 해당 월 완료된 배달 수 조회
        return deliverySalesRepository.countOrdersByRider(rider, year, month, DeliveryStatus.COMPLETED);
    }

    public int calculatePercentile(CustomUserDetails userDetails, int year, int month) {
        String userId = userDetails.getUser().getUserId();
        Rider rider = riderRepository.findByUser_UserId(userId).orElseThrow();

        List<Object[]> results = deliverySalesRepository.findAllRiderMonthlySalesAndCount(year, month);

        List<BigDecimal> avgSalesList = new ArrayList<>();
        AtomicReference<BigDecimal> targetAvgRef = new AtomicReference<>(BigDecimal.ZERO);

        for (Object[] row : results) {
            UUID riderId = (UUID) row[0];

            // row[1]은 Long이므로 Number로 캐스팅 후 BigDecimal로 변환
            BigDecimal totalSales = BigDecimal.valueOf(((Number) row[1]).longValue());

            long count = (long) row[2];

            if (count > 0) {
                BigDecimal avg = totalSales.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP);
                avgSalesList.add(avg);

                if (rider.getRiderId().equals(riderId)) {
                    targetAvgRef.set(avg);
                }
            }
        }

        BigDecimal targetAvg = targetAvgRef.get();

        long higherCount = avgSalesList.stream()
            .filter(avg -> avg.compareTo(targetAvg) > 0)
            .count();

        int percentile = (int) (((double) higherCount / avgSalesList.size()) * 100);
        return 100 - percentile;
    }




    
    
}

