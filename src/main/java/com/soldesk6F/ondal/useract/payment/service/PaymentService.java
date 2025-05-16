package com.soldesk6F.ondal.useract.payment.service;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.soldesk6F.ondal.config.SecurityConfig;
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.login.OAuth2LoginSuccessHandler;
import com.soldesk6F.ondal.owner.order.OrderService;
import com.soldesk6F.ondal.user.controller.user.DeleteUserController;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.cart.controller.CartController;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.entity.CartItemOption;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;
import com.soldesk6F.ondal.useract.cart.repository.CartItemsRepository;
import com.soldesk6F.ondal.useract.cart.repository.CartRepository;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToOwner;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
import com.soldesk6F.ondal.useract.order.repository.OrderDetailRepository;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;
import com.soldesk6F.ondal.useract.payment.dto.CartItemsDTO;
import com.soldesk6F.ondal.useract.payment.dto.TossPaymentResponse;
import com.soldesk6F.ondal.useract.payment.dto.TossRefundResponse;
import com.soldesk6F.ondal.useract.payment.dto.UserInfoDTO;
import com.soldesk6F.ondal.useract.payment.entity.Payment;
import com.soldesk6F.ondal.useract.payment.entity.Payment.PaymentMethod;
import com.soldesk6F.ondal.useract.payment.entity.Payment.PaymentStatus;
import com.soldesk6F.ondal.useract.payment.entity.PaymentFailLog;
import com.soldesk6F.ondal.useract.payment.repository.PaymentFailLogRepository;
import com.soldesk6F.ondal.useract.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CartController cartController;

    private final SecurityConfig securityConfig;

    private final OAuth2LoginSuccessHandler OAuth2LoginSuccessHandler;

    private final DeleteUserController deleteUserController;

	private final CartRepository cartRepository;
	private final CartItemsRepository cartItemsRepository;
	private final OrderRepository orderRepository;
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final OrderService orderService;
	private final OrderDetailRepository orderDetailRepository;
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;
	private final PaymentFailLogRepository paymentFailLogRepository;
	private final PaymentFailLogService paymentFailLogService;
	
	

	@Value("${toss.secret-key}")
    private String tossSecretKey;

 
	public List<CartItemsDTO> getAllCartItems(UUID cartUUID) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("로그인된 사용자가 없습니다");
		}
		User user = null;
		if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
			user = customUserDetails.getUser();
		}
		UUID nowSessionUUID;

		if (user != null) {
			nowSessionUUID = user.getUserUuid();
			Optional<Cart> optCart = cartRepository.findById(cartUUID);
			Cart cart = optCart.orElseThrow(() -> new IllegalArgumentException("해당 카트가 존재하지 않습니다."));
			if (cart.getUser().getUserUuid().equals(nowSessionUUID)) {

				List<CartItems> items = cartItemsRepository.findByCart_CartId(cart.getCartId());
				System.out.println("cartId from DB: " + cart.getCartId());

				if (items.isEmpty()) {
					throw new IllegalArgumentException("카트에 담긴 아이템이 없습니다");
				} else {
					return items.stream().map(item -> {

						List<String> options = (item.getOptions() == null) ? List.of("옵션없음")
								: Arrays.asList(item.getOptions().split("온달"));

						return CartItemsDTO.builder().menuName(item.getMenu().getMenuName())
								.menuPrice(item.getMenu().getPrice()).optionNames(options)
								.optionTotalPrice(item.getOptionTotalPrice()).quantity(item.getQuantity())
								.totalPrice(item.getItemTotalPrice()).menuImg(item.getMenu().getMenuImg()).build();

					}).collect(Collectors.toList());
				}

			}

		}
		throw new IllegalStateException("결제 요청을 처리할 수 없습니다");
	}

	public int getListTotalPrice(List<CartItemsDTO> cids) {
		int total = 0;
		for (CartItemsDTO cid : cids) {
			total += cid.getTotalPrice();
		}
		return total;
	}

	public UserInfoDTO getUserInfo(UUID cartUUID) {

		Cart cart = cartRepository.getById(cartUUID);
		if (cart == null)
			throw new IllegalStateException("해당하는 유저가 없습니다");

		return UserInfoDTO.builder().userLoc(cart.getUser().getUserSelectedAddress().getAddress())
				.userSepLoc(cart.getUser().getUserSelectedAddress().getDetailAddress())
				.userTel(cart.getUser().getUserPhone()).build();

	}

	public String getCartStore(UUID cartUUID) {

		Cart cart = cartRepository.getById(cartUUID);
		if (cart == null)
			throw new IllegalStateException("해당하는 카트가 없습니다");

		return cart.getStore().getStoreName();

	}

//	public String confirmPayment(String paymentKey, String orderId, int amount) {
//	    String url = "https://api.tosspayments.com/v1/payments/confirm";
//
//	    Map<String, Object> requestBody = new HashMap<>();
//	    requestBody.put("paymentKey", paymentKey);
//	    requestBody.put("orderId", orderId);
//	    requestBody.put("amount", amount);
//
//	    String encodedKey = Base64.getEncoder()
//	        .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
//
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setContentType(MediaType.APPLICATION_JSON);
//	    headers.set("Authorization", "Basic " + encodedKey);
//
//	    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//	    RestTemplate restTemplate = new RestTemplate();
//
//	    try {
//	        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
//
//	        if (!response.getStatusCode().is2xxSuccessful()) {
//	            throw new IllegalStateException("결제 승인 실패: " + response.getBody());
//	        }
//
//	        // 여기서 그냥 JSON 전체 문자열 리턴
//	        return "<pre>" + response.getBody() + "</pre>";
//
//	    } catch (HttpClientErrorException e) {
//	        return "<pre>토스 결제 승인 에러:\n" + e.getResponseBodyAsString() + "</pre>";
//	    }
//	}
	
	@Transactional
	public String tryOndalPay(UUID cartUUID) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null && auth.isAuthenticated()) {
		    Object principal = auth.getPrincipal();

		    if (principal instanceof CustomUserDetails userDetails) {
		        User user = userDetails.getUser();
			    Optional<Cart> optCart = cartRepository.findByUser(user);
			    Cart cart = optCart.orElseThrow(() -> new IllegalArgumentException("카트 없음"));
			    int totalPrice = cart.getTotalPrice();
			    int ondalPay = user.getOndalPay();
			    if(totalPrice>ondalPay) {
			    	return "잔액이 부족합니다\n현재 잔액:"+ondalPay+":@:실패";
			    }else {
			    	int nowOndalPay = ondalPay-totalPrice;
			    	user.setOndalPay(ondalPay);
			    	userRepository.save(user);
			    	return nowOndalPay+":@:성공";
			    }
			    
		        
		        
		    }
		    return "유저가 비회원임:@:실패";
		    
		}else {
			return "유저가 없음:@:실패";
			
		}
		
	}
	
	
	
	
	@Transactional
	public boolean confirmPayment(String paymentKey, String orderId, int amount) {
		String url = "https://api.tosspayments.com/v1/payments/confirm";

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("paymentKey", paymentKey);
		requestBody.put("orderId", orderId);
		requestBody.put("amount", amount);

		String encodedKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + encodedKey);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
		RestTemplate restTemplate = new RestTemplate();

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
			if (!response.getStatusCode().is2xxSuccessful()) {

				return false;
//	        	throw new IllegalStateException("결제 승인 실패: " + response.getBody());
			}
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			TossPaymentResponse tossResponse = null;
			try {
				tossResponse = objectMapper.readValue(response.getBody(), TossPaymentResponse.class);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			UUID cartUUID = UUID.fromString(orderId);

			Optional<Cart> optCart = cartRepository.findById(cartUUID);
			Cart cart = optCart.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카트입니다."));
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
			User user = cud.getUser();

			List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();

			Order order = null;
			for (CartItems cartItem : cart.getCartItems()) {
				OrderDetail orderDetail = new OrderDetail();

				orderDetail.setMenu(cartItem.getMenu());
				orderDetail.setPrice(cartItem.getItemTotalPrice());
				orderDetail.setQuantity(cartItem.getQuantity());
				orderDetail.setOptionNames(cartItem.getOptionsAsList());
				List<Integer> ciop = new ArrayList<Integer>();
				for (CartItemOption cartItemOption : cartItem.getCartItemOptions()) {
					ciop.add(cartItemOption.getOptionPrice());
				}
				orderDetail.setOptionPrices(ciop);
				orderDetailList.add(orderDetail);
			}

			Payment payment = new Payment();
			payment.setAmount(tossResponse.getTotalAmount());
			payment.setPaymentKey(tossResponse.getPaymentKey());
			payment.setTossOrderId(tossResponse.getOrderId());
			payment.setUser(user);
			payment.setApprovedAt(tossResponse.getApprovedAt().toLocalDateTime());
			payment.setRequestedAt(tossResponse.getRequestedAt().toLocalDateTime());
			payment.setPaymentUsageType(Payment.PaymentUsageType.ORDER_PAYMENT);
			switch (tossResponse.getMethod()) {
			case "카드":
				payment.setPaymentMethod(PaymentMethod.CREDIT);
				break;
			default:
				payment.setPaymentMethod(PaymentMethod.CASH);
				break;

			}
			switch (tossResponse.getStatus()) {
			case "DONE":
				payment.setPaymentStatus(PaymentStatus.COMPLETED);
				order = Order.builder().store(cart.getStore()).user(cart.getUser())
						.totalPrice(tossResponse.getTotalAmount())
						.storeRequest(tossResponse.getMetadata().getReqStore())
						.deliveryRequest(tossResponse.getMetadata().getReqDel()).orderDetails(orderDetailList)
						.deliveryAddress(user.getUserSelectedAddress().getAddress())
						.deliveryAddressLatitude(user.getUserSelectedAddress().getUserAddressLatitude())
						.deliveryAddressLongitude(user.getUserSelectedAddress().getUserAddressLongitude())
						.orderToOwner(OrderToOwner.PENDING).build();
				break;
			case "READY":
			case "IN_PROGRESS":
				payment.setPaymentStatus(PaymentStatus.CANCELED);
				break;
			default:
				payment.setPaymentStatus(PaymentStatus.CANCELED);
				break;
			}

			for (OrderDetail od : order.getOrderDetails()) {
				od.setOrder(order);
			}
			payment.setOrder(order);
			orderRepository.save(order);
			paymentRepository.save(payment);
			cartItemsRepository.deleteByCart_cartId(cartUUID);
			cartRepository.deleteById(cartUUID);

			return true;
		} catch (HttpClientErrorException e) {
			return false;
		}
	}

	
	
	

	@Transactional
	public void confirmOndalWalletCharge(String paymentKey, String orderId, int amount, UUID userUUID) {
	    String url = "https://api.tosspayments.com/v1/payments/confirm";

	    Map<String, Object> requestBody = new HashMap<>();
	    requestBody.put("paymentKey", paymentKey);
	    requestBody.put("orderId", orderId);
	    requestBody.put("amount", amount); // 정확한 금액 확인

	    String encodedKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.set("Authorization", "Basic " + encodedKey);

	    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
	    RestTemplate restTemplate = new RestTemplate();

	    try {
	        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

	        if (!response.getStatusCode().is2xxSuccessful()) {
	            throw new IllegalStateException("결제 승인 실패: " + response.getBody());
	        }

	        // 응답 처리
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.registerModule(new JavaTimeModule());
	        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	        TossPaymentResponse tossResponse = objectMapper.readValue(response.getBody(), TossPaymentResponse.class);

	        User user = userRepository.findById(userUUID)
	                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

	        // 결제 방식 설정
	        PaymentMethod method = tossResponse.getMethod().equals("카드") ? Payment.PaymentMethod.CREDIT
	                : Payment.PaymentMethod.CASH;

	        // Payment 엔티티 저장
	        Payment payment = Payment.builder()
	                .user(user)
	                .order(null) // 지갑 충전
	                .paymentKey(tossResponse.getPaymentKey())
	                .tossOrderId(tossResponse.getOrderId())
	                .paymentMethod(method)
	                .amount(tossResponse.getTotalAmount())
	                .paymentUsageType(Payment.PaymentUsageType.ONDAL_WALLET)
	                .paymentStatus(Payment.PaymentStatus.COMPLETED)
	                .refundReason(null)
	                .build();

	        payment.setRequestedAt(tossResponse.getRequestedAt().toLocalDateTime());
	        payment.setApprovedAt(tossResponse.getApprovedAt().toLocalDateTime());

	        // 결제 방식에 따라 설정
	        switch (tossResponse.getStatus()) {
	            case "카드":
	                payment.setPaymentMethod(Payment.PaymentMethod.CREDIT);
	                break;
	            default:
	                payment.setPaymentMethod(Payment.PaymentMethod.CASH);
	                break;
	        }

	        paymentRepository.save(payment);

	        // 온달 지갑 충전
	        user.setOndalWallet(user.getOndalWallet() + tossResponse.getTotalAmount());
	        userRepository.save(user);

	    } catch (HttpClientErrorException e) {
	        // Toss 응답에서 오류 코드와 메시지 추출
	        String errorJson = e.getResponseBodyAsString();
	        ObjectMapper mapper = new ObjectMapper();
	        String failCode = null;
	        String failMessage = null;

	        try {
	            JsonNode errorNode = mapper.readTree(errorJson);
	            failCode = errorNode.get("code").asText();
	            failMessage = errorNode.get("message").asText();

	            // 실패 로그 기록
	            paymentFailLogService.logWalletPaymentFailure(paymentKey, orderId, failCode, failMessage, userUUID);
	            System.out.println("failCode :" + failCode);
	            System.out.println("failMessage :" + failMessage);
	         // 결제 승인 에러
		        throw new IllegalArgumentException("토스 결제 승인 에러: " + e.getResponseBodyAsString(), e);
	        } catch (JsonProcessingException parseEx) {
	            failCode = "UNKNOWN_ERROR";
	            failMessage = "응답 파싱 실패";
	            // 파싱 실패 시 기본 메시지라도 기록
	            paymentFailLogService.logWalletPaymentFailure(paymentKey, orderId, failCode, failMessage, userUUID);
	        }

	    } catch (JsonProcessingException e) {
	        // JSON 파싱 실패 처리
	        throw new IllegalStateException("토스 응답 파싱 실패", e);
	    
	}

	}	
	
	
	
	
	
	@Transactional
	public void refundTossPayment(String paymentKey, String cancelReason) {
	    String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    String secretKey = tossSecretKey;
	    String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
	    headers.set("Authorization", "Basic " + encodedAuth);

	    Map<String, String> body = new HashMap<>();
	    body.put("cancelReason", cancelReason);

	    HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
	    RestTemplate restTemplate = new RestTemplate();

	    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
	    ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TossRefundResponse tossRefundResponse;
		try {
			tossRefundResponse = om.readValue(response.getBody(),TossRefundResponse.class);
			switch(tossRefundResponse.getStatus()) {
			case "COMPLETED":
				paymentRepository.updatePaymentStatusWithPaymentKey(paymentKey, Payment.PaymentStatus.COMPLETED);
				break;
			case "PENDING":
				paymentRepository.updatePaymentStatusWithPaymentKey(paymentKey, Payment.PaymentStatus.WAITING_FOR_REFUND);
				break;
			case "CANCELED":
				paymentRepository.updatePaymentStatusWithPaymentKey(paymentKey, Payment.PaymentStatus.REFUNDED);
			default:
				System.out.println("여기서 걸림");
				break;
			}
			
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
        
	    
	    
	    System.out.println("환불 응답: " + response.getBody());
	    
	    
	}	
	
	
	    
}
