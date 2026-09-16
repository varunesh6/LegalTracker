package com.legaltrack.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subDirectory);
    Resource loadFileAsResource(String storageKey);
    void deleteFile(String storageKey);
    String calculateChecksum(MultipartFile file);
    Path getFilePath(String storageKey);
}
