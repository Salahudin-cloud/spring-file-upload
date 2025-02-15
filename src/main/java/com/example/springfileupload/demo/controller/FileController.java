package com.example.springfileupload.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    @PostMapping(
            path = "/single-upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, String>> handleFileUpload(@RequestParam("file")MultipartFile file) throws IOException {
        Map<String, String> map = new HashMap<>();


        Map<String, String> response = Map.of(
                "message", "File upload done",
                "fileName", Objects.requireNonNull(file.getOriginalFilename()),
                "fileSize", String.valueOf(file.getSize()),
                "fileContentType", Objects.requireNonNull(file.getContentType())

        );

        return ResponseEntity.ok(response);
    }

}
