package com.soldesk6F.ondal.rider.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rider")
public class RiderSalesChartController {

    // 라이더 매출 차트 페이지로 이동
    @GetMapping("/riderChart")
    public String getStatisticsPage() {
        // RiderSalesChart.html 페이지로 이동
        return "/content/rider/riderChart"; 
    }
}
