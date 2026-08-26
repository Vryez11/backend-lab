package com.vryez.backendlab.lab25;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VideoAdminController {

    private final VideoAdminService videoAdminService;

    @PostMapping("/lab25/videos/hide")
    public ResponseEntity<Void> hide(@RequestBody HideRequest req) throws ModerationException {
        videoAdminService.hideReportedVideos(req.videoIds());
        return ResponseEntity.ok().build();
    }

    public record HideRequest(List<Long> videoIds) {}
}
