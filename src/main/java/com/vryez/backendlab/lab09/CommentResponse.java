package com.vryez.backendlab.lab09;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String author;
    private String content;
    private String createdAt;

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getAuthor(),
                comment.getContent(), comment.getCreatedAt().toString());
    }
}
