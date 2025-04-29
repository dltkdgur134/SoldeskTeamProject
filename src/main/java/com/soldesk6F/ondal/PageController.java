package com.soldesk6F.ondal;
 
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.owner.order.OrderService;
import com.soldesk6F.ondal.useract.order.dto.OrderHistoryDto;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {
	
	// 마이페이지 이동
	@GetMapping (value = "/myPage")
	public String goMyPage(Model model) {
		System.out.println("myPage 컨트롤러 진입");
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
            @AuthenticationPrincipal CustomUserDetails cud,
            Model model) {
    	
        // cud가 null이었으니, @AuthenticationPrincipal을 붙여서 바인딩
    	
        String userId = cud.getUser().getUserId();
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
	
}
