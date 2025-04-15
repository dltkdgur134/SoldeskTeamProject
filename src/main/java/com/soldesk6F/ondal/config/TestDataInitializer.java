package com.soldesk6F.ondal.config;


import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.menu.entity.Menu.MenuStatus;
import com.soldesk6F.ondal.menu.repository.MenuRepository;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.entity.Store.StoreStatus;
import com.soldesk6F.ondal.store.repository.StoreRepository;
import com.soldesk6F.ondal.user.entity.Owner;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.User.UserRole;
import com.soldesk6F.ondal.user.entity.User.UserStatus;
import com.soldesk6F.ondal.user.repository.OwnerRepository;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.order.entity.Order;
import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
import com.soldesk6F.ondal.useract.order.entity.Order.OrderStatus;
import com.soldesk6F.ondal.useract.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OwnerRepository OwnerRepository;
    // RiderRepository, etc. 필요하면 주입

    @Override
    public void run(String... args) throws Exception {

        // 1) 일반 User 생성
        User user1 = User.builder()
                .userId("user1")
                .password("1234")
                .userName("홍길동")
                .nickName("길동이")
                .email("user1@example.com")
                .userPhone("010-1111-2222")
                .userRole(UserRole.USER)       // 일반 유저
                .userStatus(UserStatus.ACTIVE) // 정상 계정
                .socialLoginProvider("NONE")
                .build();
        userRepository.save(user1);

        // 2) 점주 Owner 전용 User 생성
        User user2 = User.builder()
                .userId("owner1")
                .password("zzzz")
                .userName("사장1")
                .nickName("사장님1")
                .email("owner1@example.com")
                .userPhone("010-9999-8888")
                .userRole(UserRole.OWNER)  // 점주 권한
                .userStatus(UserStatus.ACTIVE)
                .build();
        userRepository.save(user2);

        // 3) Owner 엔티티 생성 (user2와 연결)
        Owner owner1 = Owner.builder()
                .user(user2)
                .secondaryPassword("1234") // 2차 비밀번호(예시)
                .build();
        OwnerRepository.save(owner1);

        // 4) Store 생성 (Owner 참조)
        Store store1 = Store.builder()
                .owner(owner1)
                .storeName("테스트 가게1")
                .category("Korean")
                .storePhone("02-1234-5678")
                .storeAddress("서울시 강남구 역삼동 123")
                .latitude(37.495)
                .longitude(127.027)
                .deliveryRange(3.0)
                .storeIntroduce("맛있게 만들어 드립니다!")
                .openingTime(LocalTime.of(9, 0))
                .closingTime(LocalTime.of(22, 0))
                .holiday("연중무휴")
                .storeStatus(StoreStatus.OPEN)
                .build();
        storeRepository.save(store1);

        // 두 번째 가게(옵션)
        Store store2 = Store.builder()
                .owner(owner1)
                .storeName("테스트 가게2")
                .category("Chicken")
                .storePhone("02-2222-3333")
                .storeAddress("서울시 종로구 관철동 456")
                .latitude(37.570)
                .longitude(126.986)
                .deliveryRange(2.5)
                .storeIntroduce("치킨 맛집!")
                .openingTime(LocalTime.of(10, 30))
                .closingTime(LocalTime.of(23, 30))
                .holiday("명절 당일 휴무")
                .storeStatus(StoreStatus.OPEN)
                .build();
        storeRepository.save(store2);

        // 5) Menu 생성 (store1에 연결)
        Menu menuA = Menu.builder()
                .store(store1)
                .menuName("김치찌개")
                .description("얼큰한 김치찌개")
                .price(8000)
                .menuStatus(MenuStatus.ACTIVE)
                .build();
        menuRepository.save(menuA);

        // 또 다른 메뉴
        Menu menuB = Menu.builder()
                .store(store1)
                .menuName("된장찌개")
                .description("구수한 된장")
                .price(8500)
                .menuStatus(MenuStatus.ACTIVE)
                .build();
        menuRepository.save(menuB);

        // 치킨 메뉴
        Menu menuC = Menu.builder()
                .store(store2)
                .menuName("후라이드치킨")
                .description("바삭한 후라이드")
                .price(17000)
                .menuStatus(MenuStatus.ACTIVE)
                .build();
        menuRepository.save(menuC);
        // 4. 주문(Order) 생성
        //    Order.builder() 또는 new Order(...)로 생성할 수 있음
        Order order1 = Order.builder()
                .user(user1)
                .store(store1)
                .deliveryAddress("서울시 강남구 역삼동 123")
                .storeRequest("양파 많이 넣어주세요")
                .deliveryRequest("문 앞에 놔주세요")
                .totalPrice(0)  // 디테일로부터 계산될 것
                .orderStatus(OrderStatus.PENDING)
                .guestId(null)
                .build();

        // 5. 주문 상세(OrderDetail) 생성
        OrderDetail detail1 = OrderDetail.builder()
                .order(order1)
                .menu(menuA)
                .quantity(2)
                .optionNames(Arrays.asList("곱빼기", "추가소스"))
                .optionPrices(Arrays.asList(2000, 500))
                .build();
        // price 계산은 OrderDetail 내부 calculateTotalPrice()에서 자동 처리

        OrderDetail detail2 = OrderDetail.builder()
                .order(order1)
                .menu(menuB)
                .quantity(1)
                .optionNames(Arrays.asList("매운맛"))
                .optionPrices(Arrays.asList(0))
                .build();

        // Order에 OrderDetail들을 추가
        order1.addOrderDetail(detail1);
        order1.addOrderDetail(detail2);

        // 최종 저장(orders, order_detail)
        orderRepository.save(order1);

        // 6. 추가로 다른 상태의 주문도 만들어봄
        Order order2 = Order.builder()
                .user(user1)
                .store(store2)
                .deliveryAddress("서울시 종로구 견지동 10")
                .storeRequest("천천히 만들어주세요")
                .deliveryRequest("직접 수령")
                .orderStatus(OrderStatus.CONFIRMED)
                .build();

        OrderDetail detail3 = OrderDetail.builder()
                .order(order2)
                .menu(menuB)
                .quantity(1)
                .optionNames(null)
                .optionPrices(null)
                .build();
        order2.addOrderDetail(detail3);

        orderRepository.save(order2);

        // 서버 실행 시점에
        // store1, store2 / user1 / menuA, menuB / order1, order2
        // 등이 DB에 INSERT되어 테스트 데이터가 쌓임
    }
}