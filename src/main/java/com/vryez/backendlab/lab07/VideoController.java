package com.vryez.backendlab.lab07;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("lab07VideoController")
@RequestMapping("/lab07")
public class VideoController {

    private final VideoRepository videoRepository;

    public VideoController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    record VideoResponse(Long id, String title, long viewCount, boolean canDelete) {
    }

    @GetMapping("/videos")
    public List<VideoResponse> videos(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Member loginMember = (session == null) ? null
                : (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        boolean canDelete = loginMember != null && loginMember.getRole() == Role.ADMIN;

        return videoRepository.findAll().stream()
                .map(v -> new VideoResponse(v.getId(), v.getTitle(), v.getViewCount(), canDelete))
                .toList();
    }
}
