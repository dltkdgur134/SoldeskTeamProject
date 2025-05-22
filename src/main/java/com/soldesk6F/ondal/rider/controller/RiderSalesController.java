package com.soldesk6F.ondal.rider.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.rider.service.RiderSalesService;

@RestController
@RequestMapping("/rider")
public class RiderSalesController {

    @Autowired
    private RiderSalesService riderSalesService;

    @GetMapping("/monthly-sales-summary")
    public ResponseEntity<Map<String, Object>> getMonthlySalesSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "startMonth") int startMonth,
            @RequestParam(name = "endMonth") int endMonth) {

        Map<String, Object> response = new HashMap<>();

        try {
            for (int month = startMonth; month <= endMonth; month++) {
                BigDecimal averageSales = riderSalesService.getMonthlyAverageSales(userDetails, year, month);
                long completedOrderCount = riderSalesService.getCompletedOrderCount(userDetails, year, month);
                int percentile = riderSalesService.calculatePercentile(userDetails, year, month);

                String keyPrefix = String.format("%d-%02d", year, month);
                response.put(keyPrefix + "_averageSales", averageSales != null ? averageSales : BigDecimal.ZERO);
                response.put(keyPrefix + "_completedOrderCount", completedOrderCount);
                response.put(keyPrefix + "_percentile", percentile);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 콘솔에 에러 출력
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }

        return ResponseEntity.ok(response);
    }

}


    


