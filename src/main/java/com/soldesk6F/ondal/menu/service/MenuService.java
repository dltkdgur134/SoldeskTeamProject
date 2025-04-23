package com.soldesk6F.ondal.menu.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.soldesk6F.ondal.menu.dto.MenuDto;
import com.soldesk6F.ondal.menu.dto.MenuRegisterDto;
import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.menu.repository.MenuRepository;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {
	
	private final MenuRepository menuRepository;
	private final StoreRepository storeRepository;
	
	@Value("${upload.menu.path}")
	private String uploadMenuPath;
	
	public MenuDto convertToDto(Menu menu) {
	    return MenuDto.builder()
	            .menuId(menu.getMenuId())
	            .storeId(menu.getStore().getStoreId())
	            .menuName(menu.getMenuName())
	            .description(menu.getDescription())
	            .price(menu.getPrice())
	            .menuImg(menu.getMenuImg())
	            .menuOptions1(menu.getMenuOptions1List())
	            .menuOptions1Price(menu.getMenuOptions1PriceList())
	            .menuOptions2(menu.getMenuOptions2List())
	            .menuOptions2Price(menu.getMenuOptions2PriceList())
	            .menuOptions3(menu.getMenuOptions3List())
	            .menuOptions3Price(menu.getMenuOptions3PriceList())
	            .menuStatus(menu.getMenuStatus())
	            .build();
	}

    public List<MenuDto> getMenusByStore(Store store) {
        List<Menu> menus = menuRepository.findByStore(store);
        return menus.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    @Transactional
    public void registerMenu(UUID storeId, MenuRegisterDto dto, String userId) throws Exception {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new IllegalArgumentException("해당 점포가 없습니다."));

        String fileName = null;
        try {
            if (dto.getMenuImg() != null && !dto.getMenuImg().isEmpty()) {
                String originalFilename = dto.getMenuImg().getOriginalFilename();
                fileName = UUID.randomUUID() + "_" + originalFilename;

                String uploadPath = uploadMenuPath;
                File dir = new File(uploadPath);
                if (!dir.exists()) dir.mkdirs();

                Path path = Paths.get(uploadPath, fileName);
                Files.copy(dto.getMenuImg().getInputStream(), path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String joinedOpt1 = dto.getMenuOptions1() != null ? String.join("온달", dto.getMenuOptions1()) : null;
        List<Integer> opt1Price = dto.getMenuOptions1Price().stream()
            .map(s -> s == null || s.isBlank() ? "0" : s)
            .map(Integer::parseInt)
            .collect(Collectors.toList());
        String joinedOpt1Price = String.join("온달", opt1Price.stream().map(String::valueOf).toList());

        String joinedOpt2 = dto.getMenuOptions2() != null ? String.join("온달", dto.getMenuOptions2()) : null;
        String joinedOpt2Price = dto.getMenuOptions2Price() != null ?
            dto.getMenuOptions2Price().stream()
                .map(s -> s == null || s.isBlank() ? "0" : s)
                .map(Integer::parseInt)
                .map(String::valueOf)
                .collect(Collectors.joining("온달")) : null;

        String joinedOpt3 = dto.getMenuOptions3() != null ? String.join("온달", dto.getMenuOptions3()) : null;
        String joinedOpt3Price = dto.getMenuOptions3Price() != null ?
            dto.getMenuOptions3Price().stream()
                .map(s -> s == null || s.isBlank() ? "0" : s)
                .map(Integer::parseInt)
                .map(String::valueOf)
                .collect(Collectors.joining("온달")) : null;

        Menu menu = Menu.builder()
            .store(store)
            .menuName(dto.getMenuName())
            .description(dto.getDescription())
            .price(dto.getPrice())
            .menuImg(fileName != null ? "/img/menu/" + fileName : "/img/menu/menu_default.png")
            .menuOptions1(joinedOpt1)
            .menuOptions1Price(joinedOpt1Price)
            .menuOptions2(joinedOpt2)
            .menuOptions2Price(joinedOpt2Price)
            .menuOptions3(joinedOpt3)
            .menuOptions3Price(joinedOpt3Price)
            .menuStatus(dto.getMenuStatus())
            .build();

        menuRepository.save(menu);
    }
    
    private String saveMenuImage(MultipartFile file) {
        try {
            String uploadPath = "src/main/resources/static/img/menu/";
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadPath + fileName);
            Files.copy(file.getInputStream(), path);
            return "/img/menu/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }
    
    @Transactional
    public void editMenu(UUID storeId, MenuRegisterDto dto, String userId) throws Exception {
    	Store store = storeRepository.findById(storeId)
    		.orElseThrow(() -> new IllegalArgumentException("해당 점포가 없습니다."));

    	Menu menu = menuRepository.findById(dto.getMenuId())
    		.orElseThrow(() -> new IllegalArgumentException("수정할 메뉴가 존재하지 않습니다."));

    	String fileName = null;
    	
    	try {
    		if (dto.getMenuImg() != null && !dto.getMenuImg().isEmpty()) {
    			if (menu.getMenuImg() != null && !menu.getMenuImg().equals("/img/menu/menu_default.png")) {
    				String oldPath = uploadMenuPath + File.separator + menu.getMenuImg().substring("/img/menu/".length());
    				File oldFile = new File(oldPath);
    				if (oldFile.exists()) oldFile.delete();
    			}

    			String originalFilename = dto.getMenuImg().getOriginalFilename();
    			fileName = UUID.randomUUID() + "_" + originalFilename;

    			File dir = new File(uploadMenuPath);
    			if (!dir.exists()) dir.mkdirs();

    			Path path = Paths.get(uploadMenuPath, fileName);
    			Files.copy(dto.getMenuImg().getInputStream(), path);
    			
    			menu.setMenuImg("/img/menu/" + fileName);
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

    	String joinedOpt1 = (dto.getMenuOptions1() != null)
			? dto.getMenuOptions1().stream().map(String::trim).filter(s -> !s.isEmpty())
				.collect(Collectors.joining("온달")) : null;

		String joinedOpt1Price = (dto.getMenuOptions1Price() != null)
			? dto.getMenuOptions1Price().stream().map(s -> s == null || s.isBlank() ? "0" : s.trim())
				.collect(Collectors.joining("온달")) : null;

		String joinedOpt2 = (dto.getMenuOptions2() != null)
			? dto.getMenuOptions2().stream().map(String::trim).filter(s -> !s.isEmpty())
				.collect(Collectors.joining("온달")) : null;

		String joinedOpt2Price = (dto.getMenuOptions2Price() != null)
			? dto.getMenuOptions2Price().stream().map(s -> s == null || s.isBlank() ? "0" : s.trim())
				.collect(Collectors.joining("온달")) : null;

		String joinedOpt3 = (dto.getMenuOptions3() != null)
			? dto.getMenuOptions3().stream().map(String::trim).filter(s -> !s.isEmpty())
				.collect(Collectors.joining("온달")) : null;

		String joinedOpt3Price = (dto.getMenuOptions3Price() != null)
			? dto.getMenuOptions3Price().stream().map(s -> s == null || s.isBlank() ? "0" : s.trim())
				.collect(Collectors.joining("온달")) : null;

    	menu.setStore(store);
    	menu.setMenuName(dto.getMenuName());
    	menu.setDescription(dto.getDescription());
    	menu.setPrice(dto.getPrice());
    	menu.setMenuImg(fileName != null ? "/img/menu/" + fileName : menu.getMenuImg());
    	menu.setMenuOptions1(joinedOpt1);
    	menu.setMenuOptions1Price(joinedOpt1Price);
    	menu.setMenuOptions2(joinedOpt2);
    	menu.setMenuOptions2Price(joinedOpt2Price);
    	menu.setMenuOptions3(joinedOpt3);
    	menu.setMenuOptions3Price(joinedOpt3Price);
    	menu.setMenuStatus(dto.getMenuStatus());

    	menuRepository.save(menu);
    }

}
