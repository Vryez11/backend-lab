package com.vryez.backendlab.lab04;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lab04")
public class WatchPointController {

    private final WatchPointService watchPointService;

    public WatchPointController(WatchPointService watchPointService) {
        this.watchPointService = watchPointService;
    }

    @PostMapping("/watch")
    public WatchResponse watch(@RequestBody WatchRequest request) {
        return watchPointService.record(request.userId(), request.videoId(), request.watchedSeconds());
    }
}
