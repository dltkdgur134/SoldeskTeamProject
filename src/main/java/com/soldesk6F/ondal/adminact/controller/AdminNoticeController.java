package com.soldesk6F.ondal.adminact.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soldesk6F.ondal.adminact.entity.Notice;
import com.soldesk6F.ondal.adminact.service.NoticeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/setting/notice")
public class AdminNoticeController {
	
	private final NoticeService noticeService;
	
	// 공지 목록 페이지
    @GetMapping
    public String noticeList(Model model) {
        return "content/admin/noticeList"; // 공지 목록 html
    }

    // 공지 작성 폼 페이지
    @GetMapping("/write")
    public String writeForm() {
        return "content/admin/noticeWrite"; // 공지 작성 html
    }

    // 공지 저장 처리
    @PostMapping("/save")
    public String saveNotice(@RequestParam("title") String title,
            				 @RequestParam("content") String content
                             ) {


        Notice notice = Notice.builder()
                              .title(title)
                              .content(content)
                              .build();

        noticeService.save(notice);

        return "redirect:/admin/setting/notice";
    }

    // 공지 상세 페이지 (수정 폼도 겸함)
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") UUID id, Model model) {
        Notice notice = noticeService.findById(id).orElseThrow(() -> new IllegalArgumentException("공지 없음"));
        model.addAttribute("notice", notice);
        return "content/admin/noticeEdit"; // 공지 수정 html
    }

    // 공지 수정 처리
    @PostMapping("/update/{id}")
    public String updateNotice(@PathVariable("id") UUID id,
    						   @RequestParam("title") String title,
    						   @RequestParam("content") String content
                               ) {


        Notice notice = noticeService.findById(id).orElseThrow(() -> new IllegalArgumentException("공지 없음"));

        notice.setTitle(title);
        notice.setContent(content);
        noticeService.save(notice);

        return "redirect:/admin/setting/notice";
    }

    // 공지 삭제 처리
    @PostMapping("/delete/{id}")
    public String deleteNotice(@PathVariable("id") UUID id
                               ) {


        noticeService.deleteById(id);

        return "redirect:/admin/setting/notice";
    }

}
