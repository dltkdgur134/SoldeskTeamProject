package com.soldesk6F.ondal.user.controller.rider;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.dto.rider.RiderOrderDetailDTO;
import com.soldesk6F.ondal.user.dto.rider.RiderOrderMarkerDTO;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.Rider.DeliveryRange;
import com.soldesk6F.ondal.user.repository.RiderRepository;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderHomeController {

    private final RiderRepository riderRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/home")
    public String riderHomeGet(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String userId = userDetails.getUser().getUserId();

        Optional<Rider> optionalRider = riderRepository.findByUser_UserId(userId);

        if (optionalRider.isPresent()) {
            Rider rider = optionalRider.get();
            DeliveryRange range = rider.getDeliveryRange(); // 1.0, 3.0, 5.0 중 하나 (배달반경)
            double riderLat = rider.getHubAddressLatitude();
            double riderLng = rider.getHubAddressLongitude();

            List<Order> ordersWithinRadius = orderRepository.findOrdersWithinRadius(riderLat, riderLng, range.getKm());
            // 디버깅 로그
            System.out.println("✅ R	ider 불러오기 성공!");
            System.out.println("📍 위도: " + rider.getHubAddressLatitude());
            System.out.println("📍 경도: " + rider.getHubAddressLongitude());
            System.out.println("🆔 riderId: " + rider.getRiderId());
            System.out.println("📅 등록일: " + rider.getRegistrationDate());
            System.out.println("📍 선택된 반경: " + range.getKm() + " km");
         // ✅ DTO 변환
            List<RiderOrderMarkerDTO> markerDTOs = ordersWithinRadius.stream()
                .map(order -> RiderOrderMarkerDTO.builder()
                    .orderId(order.getOrderId().toString())
                    .storeLatitude(order.getStore().getStoreLatitude())
                    .storeLongitude(order.getStore().getStoreLongitude())
                    .storeName(order.getStore().getStoreName())
                    .deliveryFee(order.getDeliveryFee())
                    .build())
                .toList();
            
            model.addAttribute("ordersJson", markerDTOs);
            model.addAttribute("orders", ordersWithinRadius);
            model.addAttribute("rider", rider);
        } else {
            System.out.println("❌ Rider 정보 없음. userId = " + userId);
        }

        return "content/rider/riderHome";
    }
    
    @GetMapping("/myPage")
    public String showMyPage() {
    	return "redirect:/myPage"; // templates/user/infopage.html 이라는 뷰 파일을 반환
    }
    
    @GetMapping("/riderInfopage")
    public String showRiderMyPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
    	String userId = userDetails.getUser().getUserId();
    	
    	Optional<Rider> optionalRider = riderRepository.findByUser_UserId(userId);

        if (optionalRider.isPresent()) {
            Rider rider = optionalRider.get();
            model.addAttribute("rider", rider);
        } else {
            // 예외 처리나 에러 페이지로 이동 가능
        }
    	
    	
    	
    	return "content/rider/riderInfopage"; 
    }
    
    @GetMapping("/api/orders")
    @ResponseBody
    public List<RiderOrderDetailDTO> getRiderOrderMarkers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            String userId = userDetails.getUser().getUserId();
            Optional<Rider> optionalRider = riderRepository.findByUser_UserId(userId);
            if (optionalRider.isEmpty()) {
                throw new IllegalArgumentException("Rider 정보가 없습니다.");
            }
            Rider rider = optionalRider.get();
            DeliveryRange range = rider.getDeliveryRange();
            double riderLat = rider.getHubAddressLatitude();
            double riderLng = rider.getHubAddressLongitude();

            // 반경 내 주문들 조회
            List<Order> ordersWithinRadius = orderRepository.findOrdersWithinRadius(riderLat, riderLng, range.getKm());

            // DTO로 변환하여 반환
            return ordersWithinRadius.stream()
                .map(order -> {
                	LocalTime expectCookingTime = order.getExpectCookingTime();
                    String expectCookingTimeFormatted = expectCookingTime != null 
                        ? expectCookingTime.getMinute() + "분"
                        : "";
                    LocalDateTime orderTime = order.getOrderTime();
                    String formattedOrderTime = orderTime != null
                        ? orderTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        : "";
                    
                    // orderToRider가 null일 경우 처리
                    String orderToRiderStatus = order.getOrderToRider() != null ? order.getOrderToRider().toString() : "UNKNOWN";
                    
                    return RiderOrderDetailDTO.builder()
                        .orderId(order.getOrderId().toString())
                        .storeName(order.getStore().getStoreName())
                        .storeAddress(order.getStore().getStoreAddress())
                        .storeLatitude(order.getStore().getStoreLatitude())
                        .storeLongitude(order.getStore().getStoreLongitude())
                        .deliveryAddress(order.getDeliveryAddress())
                        .deliveryAddressLatitude(order.getDeliveryAddressLatitude())
                        .deliveryAddressLongitude(order.getDeliveryAddressLongitude())
                        .orderTimeFormatted(formattedOrderTime)
                        .deliveryRequest(order.getDeliveryRequest())
                        .deliveryFee(order.getDeliveryFee())
                        .expectCookingTimeFormatted(expectCookingTimeFormatted)
                        .orderToRider(orderToRiderStatus)  // null인 경우 "UNKNOWN" 반환
                        .build();
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();  // 예외 메시지 출력
            throw new RuntimeException("서버 처리 중 오류 발생", e);
        }
    }

    @PostMapping("/api/orders/assign")
    @ResponseBody
    public ResponseEntity<?> assignOrderToRider(@RequestBody Map<String, String> payload) {
        String orderId = payload.get("orderId");
        Optional<Order> optionalOrder = orderRepository.findById(UUID.fromString(orderId));
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 주문이 없습니다.");
        }

        Order order = optionalOrder.get();
        order.setOrderToRider(Order.OrderToRider.DISPATCHED);  // 상태 변경
        orderRepository.save(order);
        
        return ResponseEntity.ok().build();
    }



    
}


