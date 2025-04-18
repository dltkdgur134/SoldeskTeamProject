package com.soldesk6F.ondal;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.store.entity.StoreDto;
import com.soldesk6F.ondal.store.entity.StoreRegisterDto;
import com.soldesk6F.ondal.store.service.StoreService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/api/stores/{category}")
    @ResponseBody
    public List<StoreDto> getStoresByCategory(@PathVariable("category") String category) {
        return storeService.getStoresByCategory(category);
    }
    
    @PostMapping("/store/register")
    public String registerStore(@ModelAttribute StoreRegisterDto dto,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model) {
        try {
            storeService.registerStore(dto, userDetails.getUser());
            return "redirect:/content/registerSuccess";
        } catch (Exception e) {
            model.addAttribute("error", "점포 등록 중 오류가 발생했습니다.");
            return "content/storeRegSubmit";
        }
    }
    
}




