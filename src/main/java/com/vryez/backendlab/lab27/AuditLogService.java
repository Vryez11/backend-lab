package com.vryez.backendlab.lab27;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("lab27AuditLogService")
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    // 감사 규정: 제목은 원문 그대로 남긴다(가공 금지).
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(long videoId, String uploaderId, String videoTitle) {
        auditLogRepository.save(videoId, uploaderId, videoTitle);
    }
}
