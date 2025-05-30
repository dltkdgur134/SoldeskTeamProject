package com.soldesk6F.ondal.menu.dto;

import java.util.UUID;

import com.soldesk6F.ondal.menu.entity.Menu;

import lombok.Data;

@Data
public class MenuResponseDto {
    private UUID menuId;
    private String menuName;

    public MenuResponseDto(UUID menuId, String menuName) {
        this.menuId = menuId;
        this.menuName = menuName;
    }

    public static MenuResponseDto from(Menu menu) {
        return new MenuResponseDto(menu.getMenuId(), menu.getMenuName());
    }
}