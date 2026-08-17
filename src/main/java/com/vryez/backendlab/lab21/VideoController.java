package com.vryez.backendlab.lab21;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("lab21VideoController")
@RequestMapping("/lab21/videos")
public class VideoController {

    private final VideoRepository repository;

    public VideoController(VideoRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<VideoResponse> upload(
            @Valid @RequestBody VideoUploadRequest request
    ) {

        long savedId = repository.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new VideoResponse(
                        savedId,
                        request.getHandle(),
                        request.getTitle(),
                        request.getCategory(),
                        request.getDurationSec()
                ));
    }
}
