package com.soldesk6F.ondal;

import com.soldesk6F.ondal.store.entity.StoreDto;
import com.soldesk6F.ondal.store.service.StoreService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/api/stores/{category}")
    @ResponseBody
    public List<StoreDto> getStoresByCategory(@PathVariable("category") String category) {
        return storeService.getStoresByCategory(category);
    }
}




