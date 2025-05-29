package com.soldesk6F.ondal;
 
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.owner.order.OrderService;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.user.service.UserService;
import com.soldesk6F.ondal.useract.complain.entity.Complain;
import com.soldesk6F.ondal.useract.complain.entity.Complain.ComplainStatus;
import com.soldesk6F.ondal.useract.complain.entity.Complain.Role;
import com.soldesk6F.ondal.useract.complain.repository.ComplainRepository;
import com.soldesk6F.ondal.useract.order.dto.OrderHistoryDto;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;
import com.soldesk6F.ondal.useract.payment.dto.PaymentHistoryDTO;
import com.soldesk6F.ondal.useract.payment.service.PaymentHistoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {
	
	private final UserService userService;
	// 마이페이지 이동
	@GetMapping (value = "/myPage")
	public String goMyPage(@AuthenticationPrincipal CustomUserDetails userDetails,Model model) {
		UUID userUuid = UUID.fromString(userDetails.getUser().getUserUuidAsString());
		String userId = userDetails.getUser().getUserId();
        User freshUser = userRepository.findById(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        model.addAttribute("ondalWallet", freshUser.getOndalWallet());
        model.addAttribute("ondalPay", freshUser.getOndalPay());
        model.addAttribute("userSelectedAddress", freshUser.getUserSelectedAddress());
        model.addAttribute("userRole", freshUser.getUserRole().name());
    	model.addAttribute("riderRequested", freshUser.isRiderRequested());
    	model.addAttribute("ownerRequested", freshUser.isOwnerRequested());
		System.out.println("myPage 컨트롤러 진입");
		 userService.refreshUserAuthentication(userId);
		return "content/myPage";
	}
	
	// 비밀번호 변경 페이지 이동
	@GetMapping (value = "/mySecurity")
	public String enterPass() {
		return "content/mySecurity";
	}
	
	// 자주 묻는 질문 이동
	@GetMapping("/faqs")
	public String goFAQs() {
		return "content/faq";
	}
	
	private final OrderService orderService;

    @GetMapping("/orderHistory")
    public String orderHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
    	
        // cud가 null이었으니, @AuthenticationPrincipal을 붙여서 바인딩
    	
        String userId = userDetails.getUser().getUserId();
        List<OrderHistoryDto> history = orderService.getOrderHistoryByUser(userId);
        model.addAttribute("history", history);
        return "content/orderHistory";
    }
    
	// 회원탈퇴 안내사항 페이지 이동
	@GetMapping("/deleteUserPage")
	public String goDeleteUserPage() {
		return "content/deleteUserPage";
	}
	
	// 주소 등록 페이지 이동
	@GetMapping(value = "/regAddress")
	public String goRegAddress() {
		return "content/regAddress";
	}
	
	
	private final UserRepository userRepository;
	
	// 온달 페이 이동
	@GetMapping("/ondalPay")
	public String goOndalPay(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
	    UUID userUuid = UUID.fromString(userDetails.getUser().getUserUuidAsString());
	    User freshUser = userRepository.findById(userUuid).orElseThrow();
	    
	    model.addAttribute("ondalWallet", freshUser.getOndalWallet());
	    model.addAttribute("ondalPay", freshUser.getOndalPay());

	    return "content/user/pay/ondalPay";
	}
	// 온달 페이 이동
	@GetMapping("/userWallet")
	public String goUserWallet(@AuthenticationPrincipal CustomUserDetails userDetails, Model model
			) {
		UUID userUuid = UUID.fromString(userDetails.getUser().getUserUuidAsString());
		User freshUser = userRepository.findById(userUuid).orElseThrow();
		
		model.addAttribute("ondalWallet", freshUser.getOndalWallet());
		
		return "content/user/pay/UserWallet";
	}
	
	private final PaymentHistoryService paymentHistoryService;
	// 온달 지갑 충전 결제 내역
	@GetMapping("/userPayHistory")
	public String getUserPayHistoryPage(
	    @AuthenticationPrincipal CustomUserDetails userDetails,
	    @RequestParam(name = "status" ,required = false, defaultValue = "ALL") String status,
	    @RequestParam(name = "days" ,required = false) Integer days,
	    @RequestParam(name = "usage",required = false) String usage,
	    Model model) {

	    // status, days, usage 값 로그 찍기 확인
	    System.out.println("status = " + status + ", days = " + days + ", usage = " + usage);

	    String userUUID = userDetails.getUser().getUserUuidAsString();

	    List<PaymentHistoryDTO> filteredList = paymentHistoryService.getFilteredPaymentHistory(userUUID, status, usage, days);

	    model.addAttribute("payHistoryList", filteredList);

	    // 뷰에 필터 현재 상태도 넘겨줘야 클라이언트에서 필터 버튼 상태 유지 가능
	    model.addAttribute("currentStatus", status);
	    model.addAttribute("currentDays", days);
	    model.addAttribute("currentUsage", usage);

	    return "content/user/pay/userPayHistory";  // JSP나 Thymeleaf 뷰 이름
	}

	private final OrderRepository orderRepository;
	
	@GetMapping("/userOrderLive/{orderId}")
	public String goUserOrderLive(
	        @PathVariable("orderId") UUID orderId,
	        @AuthenticationPrincipal CustomUserDetails userDetails,
	        Model model) {

	    UUID userUuid = UUID.fromString(userDetails.getUser().getUserUuidAsString());
	    User freshUser = userRepository.findById(userUuid).orElseThrow();

	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다"));

	    model.addAttribute("user", freshUser);
	    model.addAttribute("userUuid", userDetails.getUser().getUserUuidAsString());
	    model.addAttribute("order", order); // 특정 주문 정보도 전달

	    return "content/orderLive";
	}

	private final ComplainRepository complainRepository;
	
	@GetMapping("/complains")
	public String viewComplains(
	        @RequestParam(value = "keyword", required = false) String keyword,
	        @RequestParam(value = "role", required = false) Role role,
	        @RequestParam(value = "userId", required = false) String userId,
	        @RequestParam(value = "complainStatus", required = false) ComplainStatus complainStatus,
	        Model model
	) {
	    List<Complain> complains = complainRepository.findAll(); // 일단 전부 불러옴 (단순한 구조)

	    // 필터링 적용
	    if (userId != null && !userId.isBlank()) {
	        complains = complains.stream()
	                .filter(c -> c.getUser() != null && userId.equals(c.getUser().getUserId()))
	                .toList();
	    }

	    if (keyword != null && !keyword.isBlank()) {
	        complains = complains.stream()
	                .filter(c -> c.getComplainTitle() != null && c.getComplainTitle().contains(keyword))
	                .toList();
	    }

	    if (role != null) {
	        complains = complains.stream()
	                .filter(c -> c.getRole() == role)
	                .toList();
	    }

	    if (complainStatus != null) {
	        complains = complains.stream()
	                .filter(c -> c.getComplainStatus() == complainStatus)
	                .toList();
	    }

	    // 로그 확인
	    complains.forEach(c -> {
	        System.out.println("제목: " + c.getComplainTitle());
	        System.out.println("유저: " + (c.getUser() != null ? c.getUser().getUserId() : "비회원"));
	        System.out.println("상태: " + c.getComplainStatus());
	    });

	    model.addAttribute("complains", complains);
	    return "content/user/complain/complainList";
	}
	
	
}
