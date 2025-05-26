package com.soldesk6F.ondal.useract.complain;


import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.soldesk6F.ondal.admin.entity.Admin;
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.useract.complain.dto.ComplainDto;
import com.soldesk6F.ondal.useract.complain.dto.ComplainSearchCond;
import com.soldesk6F.ondal.useract.complain.dto.ReplySavedDto;
import com.soldesk6F.ondal.useract.complain.service.ComplainService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/complains")
@RequiredArgsConstructor
public class ComplainApi {

    private final ComplainService service;

    /* === 목록 === */
    @GetMapping
    public Page<ComplainDto> list(
            @RequestParam(name = "role",    required = false) String role,
            @RequestParam(name = "keyword", required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdDate", direction = Direction.DESC)
            Pageable pageable) {

        return service.list(new ComplainSearchCond(role, keyword), pageable);
    }

    /* === 답변 === */
    @PostMapping(
            value    = "/{id}/reply",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReplySavedDto> reply(
            @PathVariable("id") UUID id,
            @RequestPart("content") String content,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal Admin admin ) throws IOException {
        if (admin == null)                                     // 미로그인
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String adminId = admin.getLoginId();
        ReplySavedDto dto = service.reply(id, adminId, content, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
