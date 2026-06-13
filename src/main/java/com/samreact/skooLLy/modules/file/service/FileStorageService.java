package com.samreact.skooLLy.modules.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    // Where files are saved on disk — configure in application.yml
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // The base URL your frontend uses to load images
    @Value("${app.upload.base-url:http://localhost:8080}")
    private String baseUrl;

    public String store(MultipartFile file, String folder) {
        try {
            validateFile(file);

            // Build the target directory: uploads/schools/ or uploads/avatars/
            Path targetDir = Paths.get(uploadDir, folder);
            Files.createDirectories(targetDir);

            // Generate a unique filename — never trust the original name
            String extension = getExtension(file.getOriginalFilename());
            String filename   = UUID.randomUUID() + "." + extension;

            Path targetPath = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Return the full accessible URL
            return baseUrl + "/uploads/" + folder + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Only allow images
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Max 5MB
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be under 10MB");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}