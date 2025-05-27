package com.soldesk6F.ondal.useract.complain.controller;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.useract.complain.dto.ComplainDetailViewDto;
import com.soldesk6F.ondal.useract.complain.dto.ComplainDetailViewDto.ReplyDto;
import com.soldesk6F.ondal.useract.complain.dto.ComplainUserDTO;
import com.soldesk6F.ondal.useract.complain.entity.Complain;
import com.soldesk6F.ondal.useract.complain.entity.ComplainImg;
import com.soldesk6F.ondal.useract.complain.repository.ComplainImgRepository;
import com.soldesk6F.ondal.useract.complain.repository.ComplainRepository;
import com.soldesk6F.ondal.useract.complain.service.ComplainService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ComplainController {

    private final ComplainService complainService;
    private final ComplainRepository complainRepository;
    private final ComplainImgRepository complainImgRepository;
    private final PasswordEncoder passwordEncoder; 

    @PostMapping("/submitComplain")
    public String submitComplain(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @ModelAttribute ComplainUserDTO complainDTO,
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
            if (password == null || !passwordEncoder.matches(password, complain.getComplainPassword())) {
            	System.out.println("입력된 password: " + password);
            	System.out.println("DB 저장된 암호화 비밀번호: " + complain.getComplainPassword());
            	System.out.println("passwordEncoder.matches 결과: " + passwordEncoder.matches(password, complain.getComplainPassword()));
            	
            	
            	return "redirect:/complains";  // 비밀번호 틀리면 리스트로 리다이렉트
            }
        }

        List<ComplainImg> complainImgList = complainImgRepository.findByComplain_ComplainId(uuid);
        
        model.addAttribute("userUuid",userUuid);
        model.addAttribute("complain", complain);
        model.addAttribute("complainImgList", complainImgList);

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

        List<ComplainImg> complainImgs = complainImgRepository.findByComplain(complain);
        model.addAttribute("complainImgs", complainImgs);
        
        model.addAttribute("complain", complain);
        return "content/user/complain/editForm";  // 수정 폼 HTML
    }

    @PostMapping("/complains/edit/{id}")
    public String updateComplain(@PathVariable("id") UUID id,
                                 @RequestParam("title") String complainTitle,
                                 @RequestParam("content") String complainContent,
                                 @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                 @RequestParam(value = "deleteImageIds", required = false) List<UUID> deleteImageIds,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {

    	try {
            complainService.updateComplainWithImages(id, complainTitle, complainContent, images, deleteImageIds, userDetails.getUser());
        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그 출력
            // 에러 페이지 보여주거나 메시지 반환 (현재는 단순 리다이렉트)
            return "redirect:/complains/edit/" + id + "?error";
        }
        return "redirect:/complains/edit/" + id;
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
    @PostMapping("/complains/{id}/checkPassword")
    public String checkPassword(
            @PathVariable("id") UUID id,
            @RequestParam(name = "password") String password,
            
            RedirectAttributes redirectAttributes
    ) {
        boolean isValid = complainService.checkPassword(id, password);
        if (isValid) {
            // 비밀번호 맞으면 상세 페이지로 이동
        	 return "redirect:/complains/" + id + "?password=" + password;
        } else {
            // 틀리면 에러 메시지 넣고 리스트 페이지로
            redirectAttributes.addFlashAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "redirect:/complains";
        }
    }
    @PostMapping("/complains/{id}/checkRepliedPassword")
    public String checkRepliedPassword(
    		@PathVariable("id") UUID id,
    		@RequestParam(name = "password") String password,
    		
    		RedirectAttributes redirectAttributes
    		) {
    	boolean isValid = complainService.checkPassword(id, password);
    	if (isValid) {
    		// 비밀번호 맞으면 상세 페이지로 이동
    		return "redirect:/complains/complainReply" + id + "?password=" + password;
    	} else {
    		// 틀리면 에러 메시지 넣고 리스트 페이지로
    		redirectAttributes.addFlashAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
    		return "redirect:/complains/complainReply";
    	}
    }
    
    @GetMapping("/complains/complainReply/{id}")
    public String viewDetail(@PathVariable("id") UUID id, Model model) {
        Complain complain = complainRepository.findById(id).orElseThrow();
        List<String> complainImgs = complain.getComplainImgs().stream()
            .map(ComplainImg::getComplainImg).toList();

        List<ReplyDto> replies = complain.getReplies().stream()
            .map(r -> new ReplyDto(
                r.getReplyContent(),
                r.getAdmin().getLoginId(),
                r.getRepliedDate()))
            .toList();

        ComplainDetailViewDto dto = new ComplainDetailViewDto(
            complain.getComplainId(),
            complain.getComplainTitle(),
            complain.getComplainContent(),
            complain.getRole().name(),
            complain.getCreatedDate(),
            complain.getComplainStatus().name(),
            complain.getUser() != null ? complain.getUser().getUserId() : complain.getGuestId(),
            complainImgs,
            replies
        );	

        model.addAttribute("complain", dto);
        model.addAttribute("complainImgList", dto.complainImgList());
        model.addAttribute("replyList", dto.replyList());
        return "content/user/complain/complainDetailWithReply";
    }
    
    
    
}
