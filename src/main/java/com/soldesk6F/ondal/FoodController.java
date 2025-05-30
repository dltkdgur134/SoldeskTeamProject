package com.soldesk6F.ondal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Controller
@RequiredArgsConstructor
public class FoodController {

	@GetMapping("/category/{type}")
    public String loadCategory(@PathVariable String type, Model model) {
        List<Store> stores = new ArrayList<>();
        if (type.equals("korean")) {
            stores.add(new Store("김치찌개집", "/css/imgs/img_kimchi.png"));
        } else if (type.equals("western")) {
            stores.add(new Store("햄버거집", "/css/imgs/img_burger.png"));
        } else if (type.equals("snack")) {
            stores.add(new Store("떡볶이집", "/css/imgs/img_tteokbokki.png"));
        }

        model.addAttribute("stores", stores);
        return "fragments/gallery :: foodGallery"; // Thymeleaf fragment만 리턴
    }

    @Getter @Setter @AllArgsConstructor
    static class Store {
        private String name;
        private String imageUrl;
    }
}
