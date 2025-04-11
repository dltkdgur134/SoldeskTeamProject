package com.soldesk6F.ondal;

import com.soldesk6F.ondal.store.entity.StoreDto;
import com.soldesk6F.ondal.store.service.StoreService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/api/stores/{category}")
    @ResponseBody
    public List<StoreDto> getStoresByCategory(@PathVariable("category") String category) {
        return storeService.getStoresByCategory(category); // 또는 findStoresByCategory()
    }
}