package com.example.springfileupload.demo.service;

import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Service
public class FileStorageManager {

    private static final String UPLOAD_DIR = "uploads";

    @Async
    @SneakyThrows
    public void save(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.write(filePath, file.getBytes());

            Thread.sleep(new Random().nextLong(4000, 8000));

            System.out.println(file.getOriginalFilename() + " is uploaded at " + LocalDateTime.now());

        } catch (IOException e) {
            System.err.println("File I/O error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted: " + e.getMessage());
        }
    }
}
