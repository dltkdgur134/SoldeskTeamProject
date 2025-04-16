//package com.soldesk6F.ondal;
//
//import com.soldesk6F.ondal.store.entity.StoreDto;
//import com.soldesk6F.ondal.store.service.StoreService;
//
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//public class StoreController {
//
//    private final StoreService storeService;
//
//    @GetMapping("/api/stores/{category}")
//    @ResponseBody
//    public List<StoreDto> getStoresByCategory(@PathVariable("category") String category) {
//        return storeService.getStoresByCategory(category);
//    }
//    
//    @PostMapping("/register")
//    public String registerStore(@ModelAttribute StoreDto storeDTO,
//                                @RequestParam("storeImgs") MultipartFile file,
//                                Model model) {
//        try {
//            storeService.registerStore(storeDTO, file);
//            return "redirect:/content/registerSuccess";
//        } catch (Exception e) {
//            model.addAttribute("error", "점포 등록 중 오류가 발생했습니다.");
//            return "content/storeRegSubmit"; // 실패 시 다시 폼으로
//        }
//    }
//    
//}
//
//
//
//
