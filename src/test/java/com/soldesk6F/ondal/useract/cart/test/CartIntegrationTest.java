package com.soldesk6F.ondal.useract.cart.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.useract.cart.entity.Cart;
import com.soldesk6F.ondal.useract.cart.entity.CartItems;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class CartIntegrationTest {

    @Autowired
    private EntityManager em;

    @Test
    void cartItems_shouldCalculateTotalPriceCorrectly() {
        // 유저 생성
        User user = User.builder()
                .userId("testuser")
                .password("password123")
                .userProfileName("testProfile")
                .userProfileExtension("jpg")
                .userProfilePath("/images/profile.jpg")
                .userName("Test User")
                .nickName("testnick")
                .email("testuser@example.com")
                .userPhone("010-1234-5678")
                .userAddress("서울시 강남구")
                .socialLoginProvider("NONE")
                .userStatus(User.UserStatus.ACTIVE)
                .build();
        em.persist(user);

        // 오너 생성 (User와 연관)
        Owner owner = Owner.builder()
                .user(user)
                .secondaryPassword("1234") // 해싱 필요
                .build();
        em.persist(owner);

        // 가게 생성
        Store store = Store.builder()
                .storeName("온달분식")
                .category("한식")
                .storePhone("010-1234-5678")
                .storeAddress("서울시 송파구 올림픽로")
                .latitude(37.123456)
                .longitude(127.123456)
                .deliveryRange(3.0)
                .storeIntroduce("맛집입니다")
                .openingTime(LocalTime.of(9, 0))
                .closingTime(LocalTime.of(22, 0))
                .storeStatus(Store.StoreStatus.OPEN)
                .owner(owner)  // Owner 추가
                .build();
        em.persist(store);

        // 메뉴 생성 (옵션 포함)
        Menu menu = Menu.builder()
                .store(store)
                .menuName("김밥")
                .description("맛있는 김밥")
                .price(3000)
                .menuOptions1("치즈온달계란온달참치")
                .menuOptions1Price("500온달300온달800")
                .menuStatus(Menu.MenuStatus.ACTIVE)
                .build();
        em.persist(menu);

        // 카트 생성
        Cart cart = Cart.builder()
                .store(store)
                .user(user)
                .build();
        em.persist(cart);

        // 카트아이템 생성 (옵션 2개 선택: 치즈, 계란)
        CartItems cartItem = CartItems.builder()
                .cart(cart)
                .menu(menu)
                .quantity(2)
                .selectedOptions(List.of("치즈", "계란"))
                .build();
        em.persist(cartItem);

        em.flush();
        em.clear();

        // 옵션 가격: 치즈 500 + 계란 300 = 800
        // 총 가격 = (기본 3000 + 옵션 800) * 2 = 7600
        assertEquals(7600, cartItem.getItemTotalPrice());
    }
}

