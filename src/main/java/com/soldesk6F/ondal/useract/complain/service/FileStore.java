package com.soldesk6F.ondal.useract.complain.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileStore {

    @Value("${app.upload-root:/img/complain}")   // application.yml에서 경로 변경 가능
    private Path root;

    public String save(MultipartFile file, String dir) throws IOException {
        String ext = Optional.ofNullable(
            StringUtils.getFilenameExtension(file.getOriginalFilename()))
            .orElse("bin");
        String name = UUID.randomUUID() + "." + ext;
        Path folder = root.resolve(dir);
        Files.createDirectories(folder);
        file.transferTo(folder.resolve(name));
        return dir + "/" + name;  // DB에는 상대경로 저장
    }
}	