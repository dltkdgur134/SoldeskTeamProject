package com.soldesk6F.ondal.useract.payment.service;

import java.nio.charset.StandardCharsets;
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
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.OwnerRepository;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.entity.CartItemOption;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;
import com.soldesk6F.ondal.useract.cart.repository.CartItemsRepository;
import com.soldesk6F.ondal.useract.cart.repository.CartRepository;
import com.soldesk6F.ondal.useract.order.dto.OrderResponseDto;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToOwner;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToRider;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderToUser;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;
import com.soldesk6F.ondal.useract.payment.dto.CartItemsDTO;
import com.soldesk6F.ondal.useract.payment.dto.TossPaymentResponse;
import com.soldesk6F.ondal.useract.payment.dto.TossRefundResponse;
import com.soldesk6F.ondal.useract.payment.dto.UserInfoDTO;
import com.soldesk6F.ondal.useract.payment.entity.Payment;
import com.soldesk6F.ondal.useract.payment.entity.Payment.PaymentMethod;
import com.soldesk6F.ondal.useract.payment.entity.Payment.PaymentStatus;
import com.soldesk6F.ondal.useract.payment.repository.PaymentRepository;
import com.soldesk6F.ondal.useract.regAddress.entity.RegAddress;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final CartRepository cartRepository;
	private final CartItemsRepository cartItemsRepository;
	private final OrderRepository orderRepository;
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;
	private final PaymentFailLogService paymentFailLogService;
	private final OwnerRepository ownerRepository;
	
	
	

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

		User user = cart.getUser();
		RegAddress addr = user.getUserSelectedAddress();
		if (addr == null) {
			throw new IllegalStateException("선택된 주소가 없습니다. 마이페이지에서 주소를 등록해주세요.");
		}

		return UserInfoDTO.builder()
				.userLoc(addr.getAddress())
				.userSepLoc(addr.getDetailAddress())
				.userTel(user.getUserPhone())
				.build();
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
	public String tryOndalPay(UUID cartUUID , String reqDel , String reqStore,int totalPrice) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null && auth.isAuthenticated()) {
		    Object principal = auth.getPrincipal();

		    if (principal instanceof CustomUserDetails userDetails) {
		        User user = userDetails.getUser();
		        User nowUser = userRepository.getById(user.getUserUuid());
			    Optional<Cart> optCart = cartRepository.findByUser(nowUser);
			    Cart cart = optCart.orElseThrow(() -> new IllegalArgumentException("카트 없음"));
			    int deliveryFee = cart.getStore().getDeliveryFee();
			    int ondalPay = nowUser.getOndalPay();
			    if(totalPrice>ondalPay) {
			    	return "잔액이 부족합니다\n현재 잔액:"+ondalPay+":@:실패";
			    }else {
			    	int nowOndalPay = ondalPay-totalPrice;
					List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
					for (CartItems cartItem : cart.getCartItems()) {
						OrderDetail orderDetail = new OrderDetail();

						orderDetail.setMenu(cartItem.getMenu());
						orderDetail.setPrice(totalPrice);
						orderDetail.setQuantity(cartItem.getQuantity());
						orderDetail.setOptionNames(cartItem.getOptionsAsList());
						List<Integer> ciop = new ArrayList<Integer>();
						List<String> cion = new ArrayList<String>();
						for (CartItemOption cartItemOption : cartItem.getCartItemOptions()) {
							ciop.add(cartItemOption.getOptionPrice());
							cion.add(cartItemOption.getOptionName());
						}
						orderDetail.setOptionNames(cion);
						orderDetail.setOptionPrices(ciop);
						orderDetailList.add(orderDetail);
					}

					Payment payment = new Payment();
					payment.setAmount(totalPrice);
					payment.setPaymentKey(null);
					payment.setTossOrderId(cartUUID.toString());
					payment.setUser(user);
					payment.setApprovedAt(LocalDateTime.now());
					payment.setRequestedAt(LocalDateTime.now());
					payment.setPaymentUsageType(Payment.PaymentUsageType.ORDER_PAYMENT);
					payment.setPaymentStatus(PaymentStatus.COMPLETED);
					payment.setPaymentMethod(Payment.PaymentMethod.ONDALPAY);
			    	paymentRepository.save(payment);
					
					
					Order order = Order.builder().store(cart.getStore()).user(cart.getUser())
								.totalPrice(totalPrice)
								.storeRequest(reqStore)
								.deliveryRequest(reqDel).orderDetails(orderDetailList)
								.deliveryAddress(user.getUserSelectedAddress().getAddress()+" "+user.getUserSelectedAddress().getDetailAddress())
								.deliveryAddressLatitude(user.getUserSelectedAddress().getUserAddressLatitude())
								.deliveryAddressLongitude(user.getUserSelectedAddress().getUserAddressLongitude())
								.orderToUser(OrderToUser.PENDING)
								.orderToOwner(OrderToOwner.PENDING)
								.deliveryFee(deliveryFee)
								.build();
					
			    	payment.setOrder(order);
					for (OrderDetail od : order.getOrderDetails()) {
						od.setOrder(order);
					}
			    	orderRepository.save(order);
			    	user.setOndalPay(nowOndalPay);
			    	userRepository.save(user);
			    	cartRepository.deleteById(cartUUID);
			    	OrderResponseDto orderResponseDto = OrderResponseDto.from(order);
			    	UUID storeId = cart.getStore().getStoreId(); // 또는 적절한 store UUID 참조

			    	
			    	Owner owner = order.getStore().getOwner();
			    	owner.setOwnerWallet(owner.getOwnerWallet()+(totalPrice-deliveryFee));
			    	ownerRepository.save(owner);
			    	
			    	
			    	
			    	// WebSocket 전송
			    	simpMessagingTemplate.convertAndSend("/topic/store/" + storeId, orderResponseDto);
			    	return nowOndalPay + ":@:" + order.getOrderId() + ":@:성공";
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
				List<String> cion = new ArrayList<String>();
				
				for (CartItemOption cartItemOption : cartItem.getCartItemOptions()) {
					ciop.add(cartItemOption.getOptionPrice());
					cion.add(cartItemOption.getOptionName());
				}
				orderDetail.setOptionNames(cion);
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
						.deliveryAddress(user.getUserSelectedAddress().getAddress()+" "+user.getUserSelectedAddress().getDetailAddress())
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
	public void refundTossPayment(String paymentKey, String cancelReason, UUID userUuid) {
	    Payment payment = paymentRepository.findByPaymentKey(paymentKey)
	        .orElseThrow(() -> new IllegalArgumentException("해당 결제 정보를 찾을 수 없습니다."));

	    // 온달 지갑 환불 시 잔액 체크 (환불 요청 전)
	    if (payment.getPaymentUsageType() == Payment.PaymentUsageType.ONDAL_WALLET) {
	        User user = userRepository.findByUserUuid(userUuid)
	            .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

	        int newBalance = user.getOndalWallet() - payment.getAmount();
	        if (newBalance < 0) throw new IllegalStateException("잔액 부족하여 환불을 진행할 수 없습니다.");
	    }

	    String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    String encodedAuth = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
	    headers.set("Authorization", "Basic " + encodedAuth);

	    Map<String, String> body = new HashMap<>();
	    body.put("cancelReason", cancelReason);

	    HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
	    RestTemplate restTemplate = new RestTemplate();

	    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

	    ObjectMapper om = new ObjectMapper();
	    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	    try {
	        TossRefundResponse tossRefundResponse = om.readValue(response.getBody(), TossRefundResponse.class);

	        // 환불 사유 저장
	        payment.setRefundReason(cancelReason);

	        switch (tossRefundResponse.getStatus()) {
	            case "COMPLETED":
	                payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
	                break;

	            case "PENDING":
	                payment.setPaymentStatus(Payment.PaymentStatus.WAITING_FOR_REFUND);
	                break;

	            case "CANCELED":
	                payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);

	                // 온달 지갑 환불 차감 처리
	                if (payment.getPaymentUsageType() == Payment.PaymentUsageType.ONDAL_WALLET) {
	                    User user = userRepository.findByUserUuid(userUuid)
	                        .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

	                    int newBalance = user.getOndalWallet() - payment.getAmount();
	                    user.setOndalWallet(newBalance);
	                    userRepository.save(user); // 변경 사항 저장
	                }
	                // 주문 결제 환불일 경우 Order의 orderToOwner 상태 변경
	                if (payment.getPaymentUsageType() == Payment.PaymentUsageType.ORDER_PAYMENT) {
	                    Order order = payment.getOrder();
	                    if (order != null) {
	                        order.setOrderToOwner(Order.OrderToOwner.CANCELED);
	                        orderRepository.save(order);
	                    } else {
	                        throw new IllegalStateException("해당 결제에 연결된 주문이 없습니다.");
	                    }
	                }
	                break;
	            default:
	                System.out.println("알 수 없는 상태: " + tossRefundResponse.getStatus());
	                break;
	        }

	        paymentRepository.save(payment); // 상태 및 환불 사유 저장

	    } catch (JsonProcessingException e) {
	        throw new RuntimeException("환불 응답 파싱 오류", e);
	    }

	    System.out.println("환불 응답: " + response.getBody());
	}

	@Transactional
	public void tryRefundOndalPay(String tossOrderId, String cancelReason, UUID userUUID) {
	    // 1. 결제 정보 조회
	    Payment payment = paymentRepository.findByTossOrderId(tossOrderId)
	        .orElseThrow(() -> new IllegalArgumentException("해당 tossOrderId의 결제 내역을 찾을 수 없습니다."));
	    
	    
	    // 2. 결제 유효성 검사
	    if (!payment.getUser().getUserUuid().equals(userUUID)) {
	        throw new SecurityException("본인의 결제 내역만 환불할 수 있습니다.");
	    }

	    if (payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
	        throw new IllegalStateException("이미 환불된 결제입니다.");
	    }

	    if (payment.getPaymentMethod() != PaymentMethod.ONDALPAY) {
	        throw new IllegalStateException("온달페이 결제만 환불 가능합니다.");
	    }
	    Order order = payment.getOrder();
	    if (order == null) {
	        throw new IllegalStateException("결제에 연결된 주문이 없습니다.");
	    }

	    Store store = order.getStore();
	    if (store == null) {
	        throw new IllegalStateException("주문에 연결된 가게 정보가 없습니다.");
	    }

	    Owner owner = store.getOwner();
	    if (owner == null) {
	        throw new IllegalStateException("가게에 연결된 점주 정보가 없습니다.");
	    }
	    // 3. 유저 정보 및 환불 금액 확보
	    User user = payment.getUser();
	    int refundAmount = payment.getAmount(); // 원 단위 금액
	    // 5. 결제 상태 업데이트
	    payment.setPaymentStatus(PaymentStatus.REFUNDED);
	    payment.setRefundReason(cancelReason);
	    payment.setApprovedAt(LocalDateTime.now());
	    
	    paymentRepository.save(payment);
	    paymentRepository.flush();

	    
	    if (payment.getOrder() != null) {
	    	int deliveryFee = order.getStore().getDeliveryFee();
	    	if(order.getOrderToUser() == OrderToUser.PENDING
	    			|| order.getOrderToUser() == OrderToUser.CONFIRMED
	    			|| order.getOrderToUser() == OrderToUser.COOKING) {//배달 전 환불 요청
	    		order.setOrderToOwner(OrderToOwner.CANCELED);
	    		order.setOrderToUser(OrderToUser.CANCELED);
	    		owner.setOwnerWallet(owner.getOwnerWallet() - refundAmount);
	    		user.setOndalPay(user.getOndalPay() + refundAmount);
	    	}else if (order.getOrderToUser() == OrderToUser.DELIVERING) {// 배달 중 환불 요청
	    		Rider rider = order.getRider(); // 배차 안된 경우 null일 수 있음
	    	    if (rider == null && order.getOrderToUser() != OrderToUser.DELIVERING && order.getOrderToUser() != OrderToUser.COMPLETED) {
	    	        // rider가 필요한 로직인지 조건 확인 필요
	    	        throw new IllegalStateException("라이더 정보가 없습니다.");
	    	    }
	    		owner.setOwnerWallet(owner.getOwnerWallet() - refundAmount);
	    		rider.setRiderWallet(rider.getRiderWallet() + deliveryFee);
	    		user.setOndalPay(user.getOndalPay() + refundAmount);
	    		order.setOrderToOwner(OrderToOwner.CANCELED);
	    		order.setOrderToUser(OrderToUser.CANCELED);
	    		order.setOrderToRider(OrderToRider.INTERRUPTED);
	    	}
	    	else {// 배달 완료 후 환불 요청
	    		Rider rider = order.getRider(); // 배차 안된 경우 null일 수 있음
	    	    if (rider == null && order.getOrderToUser() != OrderToUser.DELIVERING && order.getOrderToUser() != OrderToUser.COMPLETED) {
	    	        // rider가 필요한 로직인지 조건 확인 필요
	    	        throw new IllegalStateException("라이더 정보가 없습니다.");
	    	    }
	    		rider.setRiderWallet(rider.getRiderWallet()- deliveryFee);
	    		owner.setOwnerWallet(owner.getOwnerWallet() - (refundAmount - deliveryFee));
	    		user.setOndalPay(user.getOndalPay() + refundAmount);
	    	}
	    	orderRepository.save(order);
	    	orderRepository.flush();
	    	ownerRepository.save(owner);
	    	ownerRepository.flush();
	    	userRepository.save(user);
	    	userRepository.flush();
			
		}
	    
	    
	    
	}

	
	
	    
}
