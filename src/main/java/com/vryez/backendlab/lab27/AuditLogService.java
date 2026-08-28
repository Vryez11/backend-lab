package com.vryez.backendlab.lab27;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service("lab27AuditLogService")
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    // 감사 규정: 제목은 원문 그대로 남긴다(가공 금지).
    // 업로드 트랜잭션이 커밋된 뒤에만 별도 스레드에서 적재한다 —
    // 감사 실패가 업로드를 오염시킬 수 없고, 업로드 커넥션을 붙잡지도 않는다.
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(VideoUploadedEvent event) {
        try {
            auditLogRepository.save(event.videoId(), event.uploaderId(), event.title());
        } catch (Exception e) {
            log.warn("감사 적재 건너뜀 videoId={} 이유={}", event.videoId(), e.toString());
        }
    }
}
