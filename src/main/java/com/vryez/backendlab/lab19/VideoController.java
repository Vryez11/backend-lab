package com.vryez.backendlab.lab19;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("lab19VideoController")
public class VideoController {

    private final VideoRepository repository;
    private final ThumbnailUrlAssembler assembler;

    public VideoController(VideoRepository repository, ThumbnailUrlAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/lab19/videos")
    public List<VideoResponse> videos() {
        return repository.findAll().stream()
                .map(v -> new VideoResponse(v.id(), v.title(), assembler.assemble(v.thumbnailKey()), v.viewCount()))
                .toList();
    }
}
