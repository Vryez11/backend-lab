package com.vryez.backendlab.lab14;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController("lab14VideoController")
public class VideoController {

    private final VideoListService service;

    public VideoController(VideoListService service) {
        this.service = service;
    }

    @GetMapping("/lab14/videos")
    public VideoPageResponse videos(@ModelAttribute VideoSearchCond cond) {
        return service.list(cond.getPage(), cond.getSize(), cond.getSort());
    }
}
