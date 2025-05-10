package com.soldesk6F.ondal.useract.order.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.menu.dto.MenuResponseDto;
import com.soldesk6F.ondal.menu.entity.Menu;
import com.soldesk6F.ondal.menu.service.MenuService;
import com.soldesk6F.ondal.owner.order.OrderService;
import com.soldesk6F.ondal.store.service.StoreService;
import com.soldesk6F.ondal.useract.order.dto.TestOrderRequestDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/testOrder")
@RequiredArgsConstructor
public class TestOrderController {

    private final StoreService storeService;
    private final MenuService menuService;
    private final OrderService orderService;

    @GetMapping
    public String showOrderForm(Model model) {
        model.addAttribute("stores", storeService.findAll());
        return "content/testOrder"; // 위에서 만든 HTML 페이지
    }

    @GetMapping("/menus/{storeId}")
    @ResponseBody
    public List<MenuResponseDto> getMenusByStore(@PathVariable("storeId") UUID storeId) {
        return menuService.findByStoreId(storeId).stream()
            .map(MenuResponseDto::from)
            .collect(Collectors.toList());
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute TestOrderRequestDto requestDto,
    		@AuthenticationPrincipal CustomUserDetails principal,
    		RedirectAttributes redirectAttributes) {
        UUID userId = principal.getUserId();
        orderService.createTestOrder(requestDto, userId);
        redirectAttributes.addFlashAttribute("message", "테스트 주문이 생성되었습니다!");
        return "redirect:/testOrder";
    }
    
    @PostMapping("/createMenu")
    public String createTestMenu(@RequestParam("storeId") UUID storeId,
                                 @RequestParam("menuName") String menuName,
                                 @RequestParam("price") int price) {
        menuService.createTestMenu(storeId, menuName, price);
        return "redirect:/testOrder";
    }
}
