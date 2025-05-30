package com.soldesk6F.ondal.user.controller.rider;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.dto.rider.RiderNaviDTO;
import com.soldesk6F.ondal.user.dto.rider.RiderOrderDetailDTO;
import com.soldesk6F.ondal.user.dto.rider.RiderOrderMarkerDTO;
import com.soldesk6F.ondal.user.dto.rider.RiderStatusResponse;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.Rider.DeliveryRange;
import com.soldesk6F.ondal.user.repository.RiderRepository;
import com.soldesk6F.ondal.user.service.RiderService;
import com.soldesk6F.ondal.user.service.UserService;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderHomeController {

    private final RiderRepository riderRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    
    @Autowired
    private RiderService riderService;

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
            model.addAttribute("riderId", rider.getRiderId()); // 추가 부분
            model.addAttribute("ordersJson", markerDTOs);
            model.addAttribute("orders", ordersWithinRadius);
            model.addAttribute("rider", rider);
        } else {
            System.out.println("❌ Rider 정보 없음. userId = " + userId);
        }
        userService.refreshUserAuthentication(userId);
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
    public ResponseEntity<?> assignOrderToRider(@AuthenticationPrincipal CustomUserDetails userDetails,
    		@RequestBody Map<String, String> payload,Model model) {
        String orderId = payload.get("orderId");
        String userId = userDetails.getUser().getUserId();
        Optional<Rider> optionalRider = riderRepository.findByUser_UserId(userId);
        
        if (optionalRider.isPresent()) {
            Rider rider = optionalRider.get();
            model.addAttribute("riderId", rider.getRiderId());
            rider.setRiderStatus(Rider.RiderStatus.DELIVERING);
            riderRepository.save(rider);
        } else {
            // 예외 처리나 에러 페이지로 이동 가능
        }
        // UUID 형식 검증
        if (!isValidUUID(orderId)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "잘못된 주문 ID 형식입니다."));
        }

        try {
            UUID orderUUID = UUID.fromString(orderId); // String -> UUID 변환
            Optional<Order> optionalOrder = orderRepository.findById(orderUUID);

            if (optionalOrder.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "해당 주문이 없습니다."));
            }

            Order order = optionalOrder.get();
            
            // 🔽 프론트에서 분, 초 받기
            String minuteStr = payload.get("expectMinute");
            String secondStr = payload.get("expectSecond");

            if (minuteStr != null && secondStr != null) {
                try {
                    int minute = Integer.parseInt(minuteStr);
                    int second = Integer.parseInt(secondStr);
                    LocalTime expectDeliveryTime = LocalTime.of(0, minute, second);
                    order.setExpectDeliveryTime(expectDeliveryTime);  // 🔥 예상 배달 시간 저장
                } catch (NumberFormatException | DateTimeException e) {
                    return ResponseEntity.badRequest().body(Collections.singletonMap("message", "잘못된 시간 형식입니다."));
                }
            }
            
            order.setOrderToRider(Order.OrderToRider.DISPATCHED);  // 상태 변경
            orderRepository.save(order);

            return ResponseEntity.ok(Collections.singletonMap("message", "배차 요청 성공"));
        } catch (Exception e) {
            // 예외 처리 (기타 예외 처리)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "배차 요청 중 오류 발생"));
        }
    }


    // UUID 형식 검증 메소드
    private boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid); // UUID 변환 시 오류가 나면 false 리턴
            return true;
        } catch (IllegalArgumentException e) {
            return false; // 잘못된 형식이면 false 리턴
        }
    }
    @GetMapping("/api/orders/{orderId}/navi")
    @ResponseBody
    public RiderNaviDTO getRiderNaviInfo(@PathVariable("orderId") UUID orderId) {
        try {
            // 주문 정보 조회
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (optionalOrder.isEmpty()) {
                throw new IllegalArgumentException("주문을 찾을 수 없습니다.");
            }
            Order order = optionalOrder.get();

            // 주문의 출발지 및 도착지 정보
            double storeLatitude = order.getStore().getStoreLatitude();
            double storeLongitude = order.getStore().getStoreLongitude();
            double deliveryAddressLatitude = order.getDeliveryAddressLatitude();
            double deliveryAddressLongitude = order.getDeliveryAddressLongitude();

            // RiderNaviDTO 생성하여 반환
            return RiderNaviDTO.builder()
                    .orderId(order.getOrderId().toString())
                    .storeLatitude(storeLatitude)
                    .storeLongitude(storeLongitude)
                    .deliveryAddressLatitude(deliveryAddressLatitude)
                    .deliveryAddressLongitude(deliveryAddressLongitude)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("서버 처리 중 오류 발생", e);
        }
    }
    
    @PutMapping("/{riderId}/status")
    public ResponseEntity<Void> changeRiderStatus(@PathVariable("riderId") UUID riderId) {
        riderService.changeRiderStatus(riderId);
        return ResponseEntity.ok().build();
    }
    // 상태 조회
    @GetMapping("/{riderId}")
    public ResponseEntity<RiderStatusResponse> getRider(@PathVariable("riderId") UUID riderId) {
        Rider rider = riderRepository.findById(riderId)
            .orElseThrow(() -> new RuntimeException("라이더를 찾을 수 없습니다"));

        RiderStatusResponse response = new RiderStatusResponse(
            rider.getRiderStatus().name(),
            rider.getRiderStatus().getDescription()
        );

        return ResponseEntity.ok(response);
    }
}


