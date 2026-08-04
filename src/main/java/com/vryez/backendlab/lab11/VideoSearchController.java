package com.vryez.backendlab.lab11;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/lab11/videos")
public class VideoSearchController {

    private final VideoSearchRepository repository;

    public VideoSearchController(VideoSearchRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/search")
    public List<VideoDto> search(@RequestParam String keyword) {
        return repository.searchByKeyword(keyword);
    }

    @GetMapping("/count")
    public long count() {
        return repository.count();
    }


}
