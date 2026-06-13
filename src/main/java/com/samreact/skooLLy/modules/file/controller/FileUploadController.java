// src/main/java/com/samreact/skooLLy/modules/file/controller/FileUploadController.java
package com.samreact.skooLLy.modules.file.controller;

import com.samreact.skooLLy.modules.file.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder
    ) {
        String url = fileStorageService.store(file, folder);
        return ResponseEntity.ok(Map.of("url", url));
    }
}