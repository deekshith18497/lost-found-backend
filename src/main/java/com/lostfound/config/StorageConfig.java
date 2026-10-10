
package com.lostfound.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StorageConfig {

    private static final String UPLOAD_DIRECTORY = "uploads";

    @Bean
    public Path uploadDirectory() {

        Path path = Paths.get(UPLOAD_DIRECTORY)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(path);
            return path;
        } catch (IOException exception) {
            throw new IllegalStateException(
                "Could not create upload directory: " + path,
                exception
            );
        }
    }
}
