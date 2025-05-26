package com.soldesk6F.ondal.useract.complain.controller;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.complain.dto.ComplainDTO;
import com.soldesk6F.ondal.useract.complain.entity.Complain;
import com.soldesk6F.ondal.useract.complain.repository.ComplainRepository;
import com.soldesk6F.ondal.useract.complain.service.ComplainService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ComplainController {

    private final ComplainService complainService;
    private final ComplainRepository complainRepository;
    private final UserRepository userRepository;

    @PostMapping("/submitComplain")
    public String submitComplain(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @ModelAttribute ComplainDTO complainDTO,
        @RequestParam(value = "files", required = false) List<MultipartFile> files,
        RedirectAttributes redirectAttributes
    ) {
    	System.out.println("userDetails: " + userDetails);
    	System.out.println("title: " + complainDTO.getComplainTitle());
    	System.out.println("content: " + complainDTO.getComplainContent());
    	System.out.println("files: " + files);
        try {
            complainService.submitComplain(userDetails, complainDTO, files);
            redirectAttributes.addFlashAttribute("result", 0);
            redirectAttributes.addFlashAttribute("resultMsg", "문의가 성공적으로 제출되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("result", 1);
            redirectAttributes.addFlashAttribute("resultMsg", "문의 제출 중 오류가 발생했습니다.");
        }
        return "redirect:/faqs";
    }
    
    @GetMapping("/complains/{uuid}")
    public String viewDetail(
            @PathVariable("uuid") UUID uuid,
            @RequestParam(name = "password", required = false) String password,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Complain complain = complainRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글이 없습니다."));

        String userUuid = userDetails.getUser().getUserUuidAsString();
        
        if (complain.getComplainPassword() != null) {
            if (password == null || !complain.getComplainPassword().equals(password)) {
                return "redirect:/complains/password/" + uuid;
            }
        }

        model.addAttribute("userUuid",userUuid);
        model.addAttribute("complain", complain);

        return "content/user/complain/detail";
    }

    
    @GetMapping("/complains/edit/{id}")
    public String showEditForm(@PathVariable("id") UUID id, 
    		Model model, 
    		@AuthenticationPrincipal CustomUserDetails userDetails) throws AccessDeniedException
    {
        Complain complain = complainService.findById(id);

        // 권한 체크 (작성자만 수정 가능)
        if (!complain.getUser().getUserUuid().equals(userDetails.getUser().getUserUuid())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        model.addAttribute("complain", complain);
        return "content/user/complain/editForm";  // 수정 폼 HTML
    }

    @PostMapping("/complains/edit/{id}")
    public String updateComplain(@PathVariable("id") UUID id,
                                 @RequestParam("title") String complainTitle,
                                 @RequestParam("content") String complainContent,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        complainService.updateComplain(id, complainTitle, complainContent, userDetails.getUser());
        return "redirect:/complains/" + id; // 수정 후 상세보기 페이지로 리다이렉트
    }

    @GetMapping("/complains/delete/{id}")
    public String deleteComplain(@PathVariable("id") UUID id,
    		@AuthenticationPrincipal CustomUserDetails userDetails) throws AccessDeniedException {
        Complain complain = complainService.findById(id);

        if (!complain.getUser().getUserUuid().equals(userDetails.getUser().getUserUuid())) {
            throw new AccessDeniedException("삭제 권한 없음");
        }

        complainService.deleteComplain(id);
        return "redirect:/complains"; // 전체 목록 페이지로 이동
    }
    
    
}
