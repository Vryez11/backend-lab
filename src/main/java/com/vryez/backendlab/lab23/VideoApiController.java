package com.vryez.backendlab.lab23;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lab23/videos")
public class VideoApiController {

    // 스텁 저장소: id==1만 존재
    @GetMapping("/{id}")
    public VideoResponse get(@PathVariable Long id) {
        if (id == 0L) {
            throw new IllegalStateException("stub storage failure");
        }
        if (id != 1L) {
            throw new VideoNotFoundException(id);
        }
        return new VideoResponse(1L, "스프링 입문", 610);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VideoResponse create(@RequestBody @Valid VideoCreateRequest req) {
        return new VideoResponse(2L, req.getTitle(), req.getDurationSeconds());
    }
}
