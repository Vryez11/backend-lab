package com.vryez.backendlab.lab27;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("lab27VideoUploadController")
@RequestMapping("/lab27/videos")
@RequiredArgsConstructor
public class VideoUploadController {

    private final VideoUploadService videoUploadService;

    @PostMapping
    public ResponseEntity<Map<String, Long>> upload(@Valid @RequestBody VideoUploadRequest request) {
        long videoId = videoUploadService.upload(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("videoId", videoId));
    }
}
