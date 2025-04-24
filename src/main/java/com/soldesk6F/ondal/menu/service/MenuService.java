//package com.soldesk6F.ondal.menu.service;
//
//import java.util.List;
//
//import com.soldesk6F.ondal.menu.dto.MenuDto;
//import com.soldesk6F.ondal.menu.entity.Menu;
//import com.soldesk6F.ondal.menu.repository.MenuRepository;
//
//public class MenuService {
//	
//	public MenuDto convertToDto(Menu menu) {
//	    return MenuDto.builder()
//	            .menuId(menu.getMenuId())
//	            .storeId(menu.getStore().getStoreId())
//	            .menuName(menu.getMenuName())
//	            .description(menu.getDescription())
//	            .price(menu.getPrice())
//	            .menuImg(menu.getMenuImg())
//	            .menuOptions1(menu.getMenuOptions1List())
//	            .menuOptions1Price(menu.getMenuOptions1PriceList())
//	            .menuOptions2(menu.getMenuOptions2List())
//	            .menuOptions2Price(menu.getMenuOptions2PriceList())
//	            .menuOptions3(menu.getMenuOptions3List())
//	            .menuOptions3Price(menu.getMenuOptions3PriceList())
//	            .menuStatus(menu.getMenuStatus())
//	            .build();
//	}
//	
//	private final MenuRepository menuRepository;
//
//    public List<MenuDto> getMenuDtosByStore(Store store) {
//        List<Menu> menus = menuRepository.findByStore(store);
//        return menus.stream().map(this::convertToDto).collect(Collectors.toList());
//    }
//
//    private MenuDto convertToDto(Menu menu) {
//        return MenuDto.builder()
//                .menuId(menu.getMenuId())
//                .storeId(menu.getStore().getStoreId())
//                .menuName(menu.getMenuName())
//                .description(menu.getDescription())
//                .price(menu.getPrice())
//                .menuImg(menu.getMenuImg())
//                .menuOptions1(menu.getMenuOptions1List())
//                .menuOptions1Price(menu.getMenuOptions1PriceList())
//                .menuOptions2(menu.getMenuOptions2List())
//                .menuOptions2Price(menu.getMenuOptions2PriceList())
//                .menuOptions3(menu.getMenuOptions3List())
//                .menuOptions3Price(menu.getMenuOptions3PriceList())
//                .menuStatus(menu.getMenuStatus())
//                .build();
//    }
//
//}
