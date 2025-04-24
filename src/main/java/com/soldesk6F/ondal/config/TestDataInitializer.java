//package com.soldesk6F.ondal.config;
//
//import com.soldesk6F.ondal.menu.entity.Menu;
//import com.soldesk6F.ondal.menu.entity.Menu.MenuStatus;
//import com.soldesk6F.ondal.menu.repository.MenuRepository;
//import com.soldesk6F.ondal.store.entity.Store;
//import com.soldesk6F.ondal.store.entity.Store.StoreStatus;
//import com.soldesk6F.ondal.store.repository.StoreRepository;
//import com.soldesk6F.ondal.user.entity.Owner;
//import com.soldesk6F.ondal.user.entity.User;
//import com.soldesk6F.ondal.user.repository.OwnerRepository;
//import com.soldesk6F.ondal.user.repository.UserRepository;
//import com.soldesk6F.ondal.useract.order.entity.Order;
//import com.soldesk6F.ondal.useract.order.entity.OrderDetail;
//import com.soldesk6F.ondal.useract.order.entity.Order.OrderToOwner;
//import com.soldesk6F.ondal.useract.order.repository.OrderRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class TestDataInitializer implements CommandLineRunner {
//
//    private final StoreRepository storeRepository;
//    private final UserRepository userRepository;
//    private final MenuRepository menuRepository;
//    private final OrderRepository orderRepository;
//    private final OwnerRepository ownerRepository;
//
//    @Override
//    public void run(String... args) {
//        // 기존 user1과 owner1 불러오기
//        User user = userRepository.findByUserId("user1").orElseThrow(() -> new RuntimeException("user1 없음"));
//        User ownerUser = userRepository.findByUserId("owner1").orElseThrow(() -> new RuntimeException("owner1 없음"));
//
//        // Owner 엔티티 생성
//        Owner owner = ownerRepository.save(Owner.builder()
//                .user(ownerUser)
//                .secondaryPassword("1234")
//                .ownerNickname("사장님1")
//                .build());
//
//        // Store 생성
//        Store store = storeRepository.save(Store.builder()
//                .owner(owner)
//                .businessNum("1112233344")
//                .storeName("테스트 가게")
//                .category("Korean")
//                .storePhone("02-1234-5678")
//                .storeAddress("서울시 강남구 역삼동 123")
//                .storeLatitude(37.495)
//                .storeLongitude(127.027)
//                .deliveryRange(Store.DeliveryRange.THREE_KM)
//                .storeIntroduce("맛있게 만들어 드립니다!")
//                .foodOrigin("국내산")
//                .openingTime(LocalTime.of(9, 0))
//                .closingTime(LocalTime.of(22, 0))
//                .holiday("연중무휴")
//                .storeStatus(StoreStatus.OPEN)
//                .build());
//
//        // Menu 생성
//        Menu menu = menuRepository.save(Menu.builder()
//                .store(store)
//                .menuName("김치찌개")
//                .description("얼큰한 김치찌개")
//                .price(8000)
//                .menuStatus(MenuStatus.ACTIVE)
//                .build());
//
//        // 주문 5개 생성
//        for (int i = 1; i <= 5; i++) {
//            Order order = Order.builder()
//                    .user(user)
//                    .store(store)
//                    .deliveryAddress("서울시 강남구 테헤란로 1" + i)
//                    .storeRequest("덜 맵게 해주세요")
//                    .deliveryRequest("문 앞에 놔주세요")
//                    .totalPrice(0)
//                    .orderToOwner(OrderToOwner.PENDING)
//                    .guestId(null)
//                    .orderDetails(new ArrayList<>())  // ✅ 필수
//                    .build();
//
//            OrderDetail detail = OrderDetail.builder()
//                    .order(order)
//                    .menu(menu)
//                    .quantity(1)
//                    .optionNames(Arrays.asList("곱빼기"))
//                    .optionPrices(Arrays.asList(1000))
//                    .build();
//
//            order.addOrderDetail(detail);  // 가격 계산 포함됨
//            orderRepository.save(order);
//        }
//    }
//}
