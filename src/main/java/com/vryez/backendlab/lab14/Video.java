package com.vryez.backendlab.lab14;

import java.time.LocalDateTime;

public record Video(Long id, String title, long views, LocalDateTime createdAt) {
}
