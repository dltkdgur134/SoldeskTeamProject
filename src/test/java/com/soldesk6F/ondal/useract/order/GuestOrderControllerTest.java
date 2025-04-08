package com.soldesk6F.ondal.useract.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soldesk6F.ondal.useract.order.dto.GuestOrderRequestDto;
import com.soldesk6F.ondal.useract.order.dto.GuestOrderRequestDto.OrderDetailDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GuestOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPlaceGuestOrder() throws Exception {
        // ✅ 반드시 실제 존재하는 값으로 바꿔야 함!
    	UUID storeId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID menuId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        OrderDetailDto orderDetail = new OrderDetailDto();
        orderDetail.setMenuId(menuId);
        orderDetail.setQuantity(2);
        orderDetail.setPrice(10000);
        orderDetail.setOptionNames(List.of("치즈", "콜라"));
        orderDetail.setOptionPrices(List.of(1000, 2000));

        GuestOrderRequestDto requestDto = new GuestOrderRequestDto();
        requestDto.setStoreId(storeId);
        requestDto.setDeliveryAddress("서울시 테스트구 테스트동");
        requestDto.setStoreRequest("양념은 따로");
        requestDto.setDeliveryRequest("문 앞에 놔주세요");
        requestDto.setOrderAdditional1("주문자 요청사항1");
        requestDto.setOrderAdditional2("주문자 요청사항2");
        requestDto.setOrderDetails(List.of(orderDetail));

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/guest/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .cookie(new Cookie("guest_id", "guest-test-id-1234")))
                .andExpect(status().isOk());
    }
}
