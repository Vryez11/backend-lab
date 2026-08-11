package com.vryez.backendlab.lab18;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ClipController {

    @GetMapping("/lab18/resume")
    public Map<String, Integer> resume(@RequestParam Timecode at) {
        return Map.of("seconds", at.getTotalSeconds());
    }
}
