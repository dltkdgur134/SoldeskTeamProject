package com.soldesk6F.ondal.config;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

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

import jakarta.annotation.PostConstruct;

@Component
public class TestDataInitializer {

    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;

    public TestDataInitializer(UserRepository userRepository,
                               OwnerRepository ownerRepository,
                               StoreRepository storeRepository,
                               MenuRepository menuRepository) {
        this.userRepository = userRepository;
        this.ownerRepository = ownerRepository;
        this.storeRepository = storeRepository;
        this.menuRepository = menuRepository;
    }

    @PostConstruct
    public void init() {
        // 유저 생성
        User user = User.builder()
                .userId("test222")
                .password("encodedpassword123")
                .userProfileName("profile")
                .userProfileExtension("jpg")
                .userProfilePath("/images/profile.jpg")
                .userName("홍길동")
                .nickName("테스트닉")
                .email("test@example.com")
                .userPhone("010-1234-5678")
                .userAddress("서울시 테스트구")
                .socialLoginProvider("NONE")
                .userRole(UserRole.OWNER)
                .userStatus(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        // 오너 생성
        Owner owner = Owner.builder()
                .user(user)
                .secondaryPassword("1234")
                .build();
        ownerRepository.save(owner);

        // 스토어 생성
        Store store = Store.builder()
                .owner(owner)
                .storeName("테스트 가게")
                .category("치킨")
                .storePhone("02-123-4567")
                .storeAddress("서울시 테스트구 테스트동 123")
                .latitude(37.5665)
                .longitude(126.9780)
                .deliveryRange(3.0)
                .storeIntroduce("테스트 스토어입니다.")
                .openingTime(LocalTime.of(10, 0))
                .closingTime(LocalTime.of(22, 0))
                .holiday("일요일")
                .storeStatus(StoreStatus.OPEN)
                .build();
        storeRepository.save(store);

        // 메뉴 생성
        Menu menu = Menu.builder()
                .store(store)
                .menuName("후라이드치킨")
                .description("바삭하고 맛있는 후라이드 치킨")
                .price(10000)
                .menuImgFilePath("/images/chicken.jpg")
                .menuOptions1("치즈온달")
                .menuOptions1Price("1000")
                .menuOptions2("콜라온달사이다")
                .menuOptions2Price("2000온달2500")
                .menuStatus(MenuStatus.ACTIVE)
                .build();
        menuRepository.save(menu);

        System.out.println("✅ 테스트 데이터 초기화 완료");
        System.out.println("Store ID: " + store.getStoreId());
        System.out.println("Menu ID: " + menu.getMenuId());
    }
}
