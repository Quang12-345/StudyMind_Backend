package com.studymind.service.storage;

import com.studymind.config.StorageProperties;
import com.studymind.exception.BadRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(StorageProperties storageProperties) throws IOException {
        this.uploadRoot = Path.of(storageProperties.uploadDir()).toAbsolutePath().normalize();
        Files.createDirectories(uploadRoot);
    }

    public void store(MultipartFile file, String storageKey) {
        try {
            Path target = resolve(storageKey);
            Files.createDirectories(target.getParent());
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new BadRequestException("Failed to store uploaded file");
        }
    }

    public Path load(String storageKey) {
        Path path = resolve(storageKey);
        if (!Files.exists(path)) {
            throw new BadRequestException("File not found on storage");
        }
        return path;
    }

    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(resolve(storageKey));
        } catch (IOException ex) {
            throw new BadRequestException("Failed to delete stored file");
        }
    }

    private Path resolve(String storageKey) {
        Path resolved = uploadRoot.resolve(storageKey).normalize();
        if (!resolved.startsWith(uploadRoot)) {
            throw new BadRequestException("Invalid storage key");
        }
        return resolved;
    }
}
