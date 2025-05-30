package com.soldesk6F.ondal.search.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soldesk6F.ondal.search.StoreSearchService;
import com.soldesk6F.ondal.search.StoreSortType;
import com.soldesk6F.ondal.store.entity.StoreDto;
import com.soldesk6F.ondal.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/search")
public class searchController {
	
	private final StoreSearchService storeSearchService;
	
	@GetMapping("/storeInRadiusFromIndex")
	public String searchStoreInRadius(@RequestParam("orignal") String orignalSearchQuery , @RequestParam("bestMatcher") String bestMatcher,@RequestParam("category") String category , Model model) {
		
		model.addAttribute("selectedCategory",category);
		model.addAttribute("original",orignalSearchQuery);
		model.addAttribute("bestMatcher",bestMatcher);
		
		return "content/store/storeList";
	}
	
	
	
	
	
	
	
	
	

}
