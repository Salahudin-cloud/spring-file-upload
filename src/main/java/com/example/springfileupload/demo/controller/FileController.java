package com.example.springfileupload.demo.controller;

import com.example.springfileupload.demo.service.FileStorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/v1")
public class FileController {

    private static final String UPLOAD_DIR = "uploads";


    private final  FileStorageManager fileStorageManager;

    @Autowired
    public FileController(FileStorageManager fileStorageManager) {
        this.fileStorageManager = fileStorageManager;
    }


    @PostMapping(
            path = "/single-upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, String>> handleFileUpload(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = Objects.requireNonNull(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            response.put("message", "File uploaded successfully");
            response.put("fileName", fileName);
            response.put("fileSize", String.valueOf(file.getSize()));
            response.put("fileContentType", Objects.requireNonNull(file.getContentType()));

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "File upload error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(
            path = "/multi-upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> handleFileUploadMulti(@RequestParam("files") MultipartFile[] files) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, String>> uploadedFiles = new ArrayList<>();

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile file : files) {
                String fileName = Objects.requireNonNull(file.getOriginalFilename());
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, file.getBytes());

                Map<String, String> fileDetails = new HashMap<>();
                fileDetails.put("fileName", fileName);
                fileDetails.put("fileSize", String.valueOf(file.getSize()));
                fileDetails.put("fileContentType", Objects.requireNonNull(file.getContentType()));

                uploadedFiles.add(fileDetails);
            }

            response.put("message", "Files uploaded successfully!");
            response.put("data", uploadedFiles);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "File upload error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Async
    @PostMapping(
            path = "/async-multi-upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public CompletableFuture<ResponseEntity<Map<String, Object>>> handleAsyncFileUploadMulti(@RequestParam("files") MultipartFile[] files) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> response = new HashMap<>();
            List<Map<String, String>> uploadedFiles = new ArrayList<>();

            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                for (MultipartFile file : files) {

                    fileStorageManager.save(file);

                    Map<String, String> fileDetails = new HashMap<>();
                    fileDetails.put("fileName", file.getOriginalFilename());
                    fileDetails.put("fileSize", String.valueOf(file.getSize()));
                    fileDetails.put("fileContentType", Objects.requireNonNull(file.getContentType()));

                    uploadedFiles.add(fileDetails);
                }

                response.put("message", "Files uploaded successfully!");
                response.put("data", uploadedFiles);

                return ResponseEntity.ok(response);
            } catch (IOException e) {
                response.put("message", "File upload error: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        });
    }
}