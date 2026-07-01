package com.studymind.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(
        String uploadDir,
        long maxFileSizeBytes
) {
    public StorageProperties {
        if (uploadDir == null || uploadDir.isBlank()) {
            uploadDir = "uploads";
        }
        if (maxFileSizeBytes <= 0) {
            maxFileSizeBytes = 20L * 1024 * 1024;
        }
    }
}
