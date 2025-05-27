package com.soldesk6F.ondal.owner.sales.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.owner.sales.dto.StoreSalesDto;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerSalesController {

    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @GetMapping("/sales")
    public String salesDashboard(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  Model model) {

        User user = userDetails.getUser();
        UUID ownerId = user.getOwner().getOwnerId();

        // 기본 조회 기간: 이번 달 1일 00:00:00 ~ 이번 달 말일 23:59:59.999
            LocalDate now = LocalDate.now();
            LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
            LocalDateTime end = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59, 999_999_999);

        // 소속 가게 전체 조회
        List<Store> stores = storeRepository.findByOwnerOwnerId(ownerId);
        model.addAttribute("storeCount", stores.size());

        // 총 매출 및 부가세
        Integer totalSalesRaw = orderRepository.sumSalesByOwnerIdAndPeriod(ownerId, start, end);
        int totalSales = (totalSalesRaw != null) ? totalSalesRaw : 0;
        int totalVat = (int) (totalSales * 0.1);

        model.addAttribute("totalSales", totalSales);
        model.addAttribute("totalVat", totalVat);

        // 가게별 매출 리스트 생성
        List<StoreSalesDto> storeSalesList = new ArrayList<>();
        for (Store store : stores) {
            UUID storeId = store.getStoreId();
            Integer storeSalesRaw = orderRepository.sumSalesByStoreIdAndPeriod(storeId, start, end);
            int storeSales = (storeSalesRaw != null) ? storeSalesRaw : 0;
            storeSalesList.add(new StoreSalesDto(storeId, store.getStoreName(), storeSales, "0%"));
        }
        model.addAttribute("storeSalesList", storeSalesList);

        // 최근 6개월 월별 매출 그래프
        List<String> monthLabels = new ArrayList<>();
        List<Integer> monthlySales = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            LocalDateTime from = targetMonth.atDay(1).atStartOfDay();
            LocalDateTime to = targetMonth.atEndOfMonth().atTime(23, 59, 59, 999_999_999);

            Integer monthSumRaw = orderRepository.sumSalesByOwnerIdAndPeriod(ownerId, from, to);
            int monthSum = (monthSumRaw != null) ? monthSumRaw : 0;

            monthLabels.add(targetMonth.getMonthValue() + "월");
            monthlySales.add(monthSum);
        }

        model.addAttribute("monthLabels", monthLabels);
        model.addAttribute("monthlySales", monthlySales);

        return "content/owner/sales";
    }
    
    @GetMapping("/sales/store/{storeId}")
    public String storeSalesDetail(@PathVariable("storeId") UUID storeId,
                                   Model model) {

        // 기본 기간 설정 (예: 이번 달)
    	// 기본 조회 기간: 이번 달 1일 00:00:00 ~ 이번 달 말일 23:59:59.999
        LocalDate now = LocalDate.now();
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59, 999_999_999);

        // 가게 정보 조회
        Store store = storeRepository.findById(storeId)
                                     .orElseThrow(() -> new IllegalArgumentException("Invalid store ID"));

        // 기간 내 가게 매출 합산
        Integer totalSalesRaw = orderRepository.sumSalesByStoreIdAndPeriod(storeId, start, end);
        int totalSales = (totalSalesRaw != null) ? totalSalesRaw : 0;
        int totalVat = (int) (totalSales * 0.1);

        model.addAttribute("store", store);
        model.addAttribute("totalSales", totalSales);
        model.addAttribute("totalVat", totalVat);
        model.addAttribute("start", start.toLocalDate());
        model.addAttribute("end", end.toLocalDate());

        // 월별 매출 (예: 최근 6개월)
        List<String> monthLabels = new ArrayList<>();
        List<Integer> monthlySales = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            LocalDateTime from = targetMonth.atDay(1).atStartOfDay();
            LocalDateTime to = targetMonth.atEndOfMonth().atTime(23, 59, 59, 999_999_999);

            Integer monthSumRaw = orderRepository.sumSalesByStoreIdAndPeriod(storeId, from, to);
            int monthSum = (monthSumRaw != null) ? monthSumRaw : 0;

            monthLabels.add(targetMonth.getMonthValue() + "월");
            monthlySales.add(monthSum);
        }

        model.addAttribute("monthLabels", monthLabels);
        model.addAttribute("monthlySales", monthlySales);

        return "content/owner/storeSalesDetail";
    }
    
    
    

}
