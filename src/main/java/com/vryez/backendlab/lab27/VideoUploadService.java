package com.vryez.backendlab.lab27;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("lab27VideoUploadService")
@RequiredArgsConstructor
public class VideoUploadService {

    private final VideoRepository videoRepository;
    private final VideoStatRepository videoStatRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public long upload(VideoUploadRequest req) {
        long videoId = videoRepository.save(req.getTitle(), req.getUploaderId());
        videoStatRepository.init(videoId);

        // 감사 적재는 커밋 이후로 분리(AFTER_COMMIT 리스너).
        // 트랜잭션이 롤백되면 이벤트는 소비되지 않으므로 헛 감사 로그도 남지 않는다.
        eventPublisher.publishEvent(new VideoUploadedEvent(videoId, req.getUploaderId(), req.getTitle()));

        return videoId;
    }
}
