package com.soldesk6F.ondal.useract.payment.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.owner.order.OrderService;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.entity.CartItemOption;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;
import com.soldesk6F.ondal.useract.cart.repository.CartItemsRepository;
import com.soldesk6F.ondal.useract.cart.repository.CartRepository;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
import com.soldesk6F.ondal.useract.order.repository.OrderDetailRepository;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;
import com.soldesk6F.ondal.useract.payment.dto.CartItemsDTO;
import com.soldesk6F.ondal.useract.payment.dto.TossPaymentResponse;
import com.soldesk6F.ondal.useract.payment.dto.UserInfoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final CartRepository cartRepository;
	private final CartItemsRepository cartItemsRepository;
	private final OrderRepository orderRepository;
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final OrderService orderService;
	private final OrderDetailRepository orderDetailRepository;
	
	
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
						
					    List<String> options = (item.getOptions() == null)
					            ? List.of("옵션없음")
					            : Arrays.asList(item.getOptions().split("온달"));
						
						return CartItemsDTO.builder().menuName(item.getMenu().getMenuName())
								.menuPrice(item.getMenu().getPrice())
								.optionNames(options)
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
		for(CartItemsDTO cid : cids) {
			total += cid.getTotalPrice();
		}
		return total;
	}
	
	
	public UserInfoDTO getUserInfo(UUID cartUUID){
		
		Cart cart = cartRepository.getById(cartUUID);
		if(cart == null) throw new IllegalStateException("해당하는 유저가 없습니다");
		
		return UserInfoDTO.builder()
			    .userLoc(cart.getUser().getUserSelectedAddress().getAddress())
			    .userSepLoc(cart.getUser().getUserSelectedAddress().getDetailAddress())
			    .userTel(cart.getUser().getUserPhone())
			    .build();
				
		
	}
	
	public String getCartStore (UUID cartUUID) {
		
		Cart cart = cartRepository.getById(cartUUID);
		if(cart ==null) throw new IllegalStateException("해당하는 카트가 없습니다");
		
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
	public void confirmPayment(String paymentKey, String orderId, int amount) {
	    String url = "https://api.tosspayments.com/v1/payments/confirm";

	    Map<String, Object> requestBody = new HashMap<>();
	    requestBody.put("paymentKey", paymentKey);
	    requestBody.put("orderId", orderId);
	    requestBody.put("amount", amount);

	    String encodedKey = Base64.getEncoder()
	        .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

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
	        ObjectMapper objectMapper = new ObjectMapper();
	        TossPaymentResponse tossResponse=null;
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
	        for(CartItems cartItem : cart.getCartItems()) {
	        	OrderDetail orderDetail = new OrderDetail();
	        	
	        	orderDetail.setMenu(cartItem.getMenu());
	        	orderDetail.setPrice(cartItem.getItemTotalPrice());
	        	orderDetail.setQuantity(cartItem.getQuantity());
	        	orderDetail.setOptionNames(cartItem.getOptionsAsList());
	        	List<Integer> ciop = new ArrayList<Integer>();
	        	for(CartItemOption cartItemOption : cartItem.getCartItemOptions()) {
	        		ciop.add(cartItemOption.getOptionPrice());
	        	}
	        	orderDetail.setOptionPrices(ciop);
	        	orderDetailList.add(orderDetail);
	        }
	        
	        
	        
	        order = Order.builder()
	        	    .store(cart.getStore())
	        	    .user(cart.getUser())
	        	    .totalPrice(tossResponse.getTotalAmount())
	        	    .storeRequest(tossResponse.getMetadata().getReqStore())
	        	    .deliveryRequest(tossResponse.getMetadata().getReqDel())
	        	    .orderDetails(orderDetailList)
	        	    .deliveryAddress(user.getUserSelectedAddress().getAddress())
	        	    .deliveryAddressLatitude(user.getUserSelectedAddress().getUserAddressLatitude())
	        	    .deliveryAddressLongitude(user.getUserSelectedAddress().getUserAddressLongitude())
	        	    .build();
	       for(OrderDetail od : order.getOrderDetails()) {
	    	   od.setOrder(order);
	       }     		
	       
	        orderRepository.save(order);
	        cartRepository.deleteById(cartUUID);
	        
	        
	    } catch (HttpClientErrorException e) {
	        throw new IllegalArgumentException("토스 결제 승인 에러: " + e.getResponseBodyAsString(), e);
	    }
	}
	
	
}
