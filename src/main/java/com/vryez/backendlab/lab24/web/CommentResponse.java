package com.vryez.backendlab.lab24.web;

import com.vryez.backendlab.lab24.Comment;

import java.time.LocalDateTime;

public record CommentResponse(Long id, Long videoId, String author, String content, LocalDateTime createdAt) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.id(),
                comment.videoId(),
                comment.author(),
                comment.content(),
                comment.createdAt()
        );
    }
}
