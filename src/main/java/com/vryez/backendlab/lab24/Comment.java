package com.vryez.backendlab.lab24;

import java.time.LocalDateTime;

public record Comment(Long id, Long videoId, String author, String content, LocalDateTime createdAt) {
}
