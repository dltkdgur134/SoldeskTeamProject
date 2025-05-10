package com.soldesk6F.ondal.search;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Component
public class NativeLibLoader {

    @Value("${native.lib.trieresource}")
    private Resource trieDll;

    @Value("${native.lib.pthreadresource}")
    private Resource pthreadDll;

    @Value("${native.lib.msvcrresource}")
    private Resource msvcr100DLL;

    
    @PostConstruct
    public void loadNativeLibrary() throws IOException {
        File tempDir = Files.createTempDirectory("native-dlls").toFile();

        // pthread 먼저 복사
        File msvcrFile = new File(tempDir, "msvcr100.dll");
        try (InputStream in = msvcr100DLL.getInputStream()) {
            Files.copy(in, msvcrFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        
        File pthreadFile = new File(tempDir, "pthreadVC2.dll");
        try (InputStream in = pthreadDll.getInputStream()) {
            Files.copy(in, pthreadFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        // 그 다음 trie 복사
        File trieFile = new File(tempDir, "trie.dll");
        try (InputStream in = trieDll.getInputStream()) {
            Files.copy(in, trieFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("[DEBUG] DLL temp dir: " + tempDir.getAbsolutePath());
        for (File f : tempDir.listFiles()) {
            System.out.println(" - 복사된 파일: " + f.getName());
        }
        System.load(msvcrFile.getAbsolutePath());

        // ✅ 1. pthread 명시적으로 먼저 로드
        System.load(pthreadFile.getAbsolutePath());

        // ✅ 2. 그 다음 trie.dll 로드 (의존성 문제 해결됨)
        System.load(trieFile.getAbsolutePath());
        

        System.out.println("[DEBUG] Native DLLs loaded successfully.");
    }

}