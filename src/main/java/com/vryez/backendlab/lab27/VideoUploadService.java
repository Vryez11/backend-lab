package com.vryez.backendlab.lab27;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("lab27VideoUploadService")
@RequiredArgsConstructor
public class VideoUploadService {

    private final VideoRepository videoRepository;
    private final VideoStatRepository videoStatRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public long upload(VideoUploadRequest req) {
        long videoId = videoRepository.save(req.getTitle(), req.getUploaderId());
        videoStatRepository.init(videoId);

        // 요구사항 변경(이번 스프린트): 감사 로그 적재가 실패해도 업로드는 성공해야 한다.
        try {
            auditLogService.record(videoId, req.getUploaderId(), req.getTitle());
        } catch (Exception e) {
            log.warn("감사 적재 건너뜀 videoId={} 이유={}", videoId, e.toString());
        }

        return videoId;
    }
}
