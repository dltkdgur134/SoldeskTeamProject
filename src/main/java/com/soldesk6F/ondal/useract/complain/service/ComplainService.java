package com.soldesk6F.ondal.useract.complain.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.soldesk6F.ondal.useract.complain.dto.ComplainSearchCond;
import com.soldesk6F.ondal.useract.complain.dto.ComplainUserDTO;
import com.soldesk6F.ondal.useract.complain.dto.ReplySavedDto;


import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.complain.entity.Complain;
import com.soldesk6F.ondal.useract.complain.entity.ComplainImg;
import com.soldesk6F.ondal.useract.complain.repository.ComplainImgRepository;
import com.soldesk6F.ondal.useract.complain.repository.ComplainRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ComplainService {

    @Value("${upload.complain.path}")
    private String uploadComplainDir;

    private final UserRepository userRepository;
    private final ComplainRepository complainRepository;
    private final ComplainImgRepository complainImgRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif");

    public Complain findById(UUID id) {
        return complainRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 문의글을 찾을 수 없습니다: " + id));
    }

    public void deleteComplain(UUID id) {
        complainRepository.deleteById(id);
    }

    @Transactional
    public void updateComplain(UUID id, String title, String content, User user) {
        Complain complain = complainRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 글"));

        if (!complain.getUser().getUserUuid().equals(user.getUserUuid())) {
            throw new AccessDeniedException("수정 권한 없음");
        }

        complain.setComplainTitle(title);
        complain.setComplainContent(content);
    }
    
    public boolean checkPassword(UUID id, String rawPassword) {
        Complain complain = complainRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문의글입니다."));

        return passwordEncoder.matches(rawPassword, complain.getComplainPassword());
    }
    
    
    @Transactional
    public void submitComplain(CustomUserDetails userDetails, ComplainUserDTO dto, List<MultipartFile> files) throws IOException {
        validateComplain(dto);
        validateFiles(files);

        User user = userRepository.findById(userDetails.getUser().getUserUuid())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        String encodedPassword = null;
        if (dto.getComplainPassword() != null && !dto.getComplainPassword().isBlank()) {
            encodedPassword = passwordEncoder.encode(dto.getComplainPassword());
        }
        
        Complain complain = Complain.builder()
                .user(user)
                .complainTitle(dto.getComplainTitle())
                .complainContent(dto.getComplainContent())
                .role(dto.getRole()) // ✅ 역할 저장
                .complainPassword(encodedPassword) // ✅ 비밀번호 저장
                .build();

        complainRepository.save(complain);

        saveImages(complain, files);
    }

    private void validateComplain(ComplainUserDTO dto) {
        if (dto.getComplainTitle() == null || dto.getComplainTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("제목을 입력해주세요.");
        }
        if (dto.getComplainContent() == null || dto.getComplainContent().trim().isEmpty()) {
            throw new IllegalArgumentException("내용을 입력해주세요.");
        }
    }

    public void validateFiles(List<MultipartFile> files) {
        if (files == null) return;

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue; // 빈 파일은 무시

            String originalName = file.getOriginalFilename();
            if (originalName == null) {
                throw new IllegalArgumentException("파일 이름이 존재하지 않습니다.");
            }

            String ext = FilenameUtils.getExtension(originalName).toLowerCase();
            if (!allowedExtensions.contains(ext)) {
                throw new IllegalArgumentException("허용되지 않은 파일 형식: " + ext);
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("파일 크기가 너무 큽니다. (최대 5MB)");
            }
        }
    }

    public void saveImages(Complain complain, List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) return;

        String realPath = new File(uploadComplainDir).getAbsolutePath();
        System.out.println("👉 Complain 이미지 저장 경로: " + realPath);

        File saveFolder = new File(realPath);
        if (!saveFolder.exists()) saveFolder.mkdirs();

        int count = 1;
        for (MultipartFile image : files) {
            if (image.isEmpty()) continue;

            String originalName = image.getOriginalFilename();
            if (originalName == null) continue;

            String ext = FilenameUtils.getExtension(originalName);
            String fileName = complain.getComplainUuidAsString() + "_" + count + "." + ext;

            File saveFile = new File(saveFolder, fileName);
            image.transferTo(saveFile);

            ComplainImg img = ComplainImg.builder()
                    .complain(complain)
                    .complainImg(fileName)
                    .build();

            complainImgRepository.save(img);
            count++;
        }
    }

    @Transactional
    public void updateComplainWithImages(UUID id,
                                         String title,
                                         String content,
                                         List<MultipartFile> newImages,
                                         List<UUID> deleteImageIds,
                                         User user) throws IOException {

        Complain complain = complainRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문의입니다."));

        if (!complain.getUser().getUserUuid().equals(user.getUserUuid())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        // 1. 제목과 내용 수정
        complain.setComplainTitle(title);
        complain.setComplainContent(content);

        // 2. 기존 이미지 삭제 처리
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            for (UUID imgId : deleteImageIds) {
                ComplainImg img = complainImgRepository.findById(imgId)
                    .orElseThrow(() -> new IllegalArgumentException("이미지 없음: " + imgId));

                File file = new File(uploadComplainDir, img.getComplainImg());
                if (file.exists()) file.delete();

                complainImgRepository.delete(img);
            }
        }

        // 3. 새 이미지 업로드
        validateFiles(newImages);
        saveImages(complain, newImages); // 기존 메서드 그대로 사용
    }
}
