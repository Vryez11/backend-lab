package com.vryez.backendlab.lab20;

import org.springframework.stereotype.Component;

/**
 * 접근 로그 기록기. actor는 호출 시점의 관리자 컨텍스트에서 읽는다 —
 * 관리자 컨텍스트가 비어 있으면 anonymous.
 */
@Component
public class AccessLogRecorder {

    public static final String ANONYMOUS = "anonymous";

    private final AccessLogRepository repository;

    public AccessLogRecorder(AccessLogRepository repository) {
        this.repository = repository;
    }

    public void record(String path) {
        String adminId = AdminContextHolder.get();
        String actor = (adminId != null) ? adminId : ANONYMOUS;
        repository.save(new AccessLog(path, actor));
    }
}
