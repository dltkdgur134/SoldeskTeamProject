package com.soldesk6F.ondal;
 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
	
	@GetMapping (value = "/myPage")
	public String goMyPage(Model model) {
		System.out.println("myPage 컨트롤러 진입");
		return "content/myPage";
	}
	
	@GetMapping (value = "/mySecurity")
	public String enterPass() {
		return "content/mySecurity";
	}
	
}
