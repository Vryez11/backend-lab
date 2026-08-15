package com.vryez.backendlab.lab20;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

@Component
public class AccessLogRepository {

    private final List<AccessLog> logs = new CopyOnWriteArrayList<>();

    public void save(AccessLog log) {
        logs.add(log);
    }

    public List<AccessLog> findAll() {
        return List.copyOf(logs);
    }

    public AccessLog findLast() {
        List<AccessLog> snapshot = findAll();
        return snapshot.isEmpty() ? null : snapshot.get(snapshot.size() - 1);
    }

    public void clear() {
        logs.clear();
    }
}
