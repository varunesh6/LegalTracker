package com.legaltrack.storage;

import com.legaltrack.config.StorageConfig;
import com.legaltrack.exception.FileValidationException;
import com.legaltrack.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private final StorageConfig storageConfig;
    private Path rootLocation;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "jpg", "jpeg", "png", "txt"
    );

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(storageConfig.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
            Files.createDirectories(this.rootLocation.resolve("cases"));
            Files.createDirectories(this.rootLocation.resolve("legal-aid"));
            Files.createDirectories(this.rootLocation.resolve("chat"));
            Files.createDirectories(this.rootLocation.resolve("verifications"));
        } catch (IOException e) {
            log.error("Could not initialize storage directory", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        if (file.isEmpty()) {
            throw new FileValidationException("Failed to store empty file.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "document");
        String extension = getFileExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileValidationException("File type not allowed. Allowed formats: PDF, DOC, DOCX, JPG, PNG, TXT");
        }

        try {
            if (originalFilename.contains("..")) {
                throw new FileValidationException("Cannot store file with relative path outside current directory " + originalFilename);
            }

            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path targetDir = this.rootLocation.resolve(subDirectory);
            Files.createDirectories(targetDir);

            Path targetLocation = targetDir.resolve(uniqueFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return subDirectory + "/" + uniqueFilename;
        } catch (IOException ex) {
            throw new FileValidationException("Could not store file " + originalFilename + ". Please try again!");
        }
    }

    @Override
    public Resource loadFileAsResource(String storageKey) {
        try {
            Path filePath = this.rootLocation.resolve(storageKey).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found " + storageKey);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found " + storageKey);
        }
    }

    @Override
    public void deleteFile(String storageKey) {
        try {
            Path filePath = this.rootLocation.resolve(storageKey).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.error("Could not delete file with storage key: " + storageKey, ex);
        }
    }

    @Override
    public String calculateChecksum(MultipartFile file) {
        try {
            return DigestUtils.md5DigestAsHex(file.getInputStream());
        } catch (IOException e) {
            log.error("Could not calculate checksum", e);
            return "";
        }
    }

    @Override
    public Path getFilePath(String storageKey) {
        return this.rootLocation.resolve(storageKey).normalize();
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
