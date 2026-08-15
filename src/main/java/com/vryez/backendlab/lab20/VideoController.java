package com.vryez.backendlab.lab20;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("lab20VideoController")
@RequestMapping("/lab20")
public class VideoController {

    private final VideoService videoService;
    private final AccessLogRecorder accessLogRecorder;

    public VideoController(VideoService videoService, AccessLogRecorder accessLogRecorder) {
        this.videoService = videoService;
        this.accessLogRecorder = accessLogRecorder;
    }

    /** 관리자 전용: 영상 삭제. 없는 영상이면 에러. */
    @PostMapping("/admin/videos/{id}/delete")
    public Map<String, Object> deleteVideo(@PathVariable Long id) {
        accessLogRecorder.record("/lab20/admin/videos/" + id + "/delete");
        videoService.delete(id);
        return Map.of("deleted", id);
    }

    /** 공개: 영상 조회. */
    @GetMapping("/videos/{id}")
    public VideoResponse getVideo(@PathVariable Long id) {
        accessLogRecorder.record("/lab20/videos/" + id);
        Video video = videoService.find(id);
        return new VideoResponse(video.id(), video.title());
    }

    /** 공개: 영상 신고. */
    @PostMapping("/videos/{id}/reports")
    public Map<String, Object> reportVideo(@PathVariable Long id) {
        accessLogRecorder.record("/lab20/videos/" + id + "/reports");
        videoService.find(id);
        return Map.of("reported", id);
    }

    public record VideoResponse(Long id, String title) {
    }
}
