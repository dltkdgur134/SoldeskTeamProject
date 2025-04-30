package com.soldesk6F.ondal.useract.review.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
import com.soldesk6F.ondal.useract.order.repository.OrderDetailRepository;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	
	@Transactional
	public void getOrderForReview(CustomUserDetails userDetails,
			UUID orderId,
			RedirectAttributes redirectAttributes,
			Model model) {
		try {
			String orderUUIDString = orderId.toString();
			UUID orderUuid = UUID.fromString(orderUUIDString);
			Optional<Order> findOrder = orderRepository.findById(orderUuid);
			
			if (findOrder.isEmpty()) {
				redirectAttributes.addFlashAttribute("result", 1);
				redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 주문 내역입니다.");
			} else {
//				String userUUIDString = userDetails.getUser().getUserUuidAsString();
//				UUID userUuid = UUID.fromString(userUUIDString);
//				Optional<User> findUser = userRepository.findById(userUuid);
//				User user = findUser.get();
				
				Order order = findOrder.get();
//				order.getUser().getUserUuid().equals(userUuid);
				
				List<OrderDetail> orderDetail = orderDetailRepository.findByOrder(order);
				model.addAttribute("order", order);
				model.addAttribute("orderDetail", orderDetail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
