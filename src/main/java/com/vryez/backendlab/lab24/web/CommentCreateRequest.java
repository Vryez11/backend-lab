package com.vryez.backendlab.lab24.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
        @NotBlank(message = "작성자는 비어 있을 수 없습니다")
        @Size(max = 30, message = "작성자는 30자를 넘을 수 없습니다")
        String author,

        @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다")
        @Size(max = 500, message = "댓글 내용은 500자를 넘을 수 없습니다")
        String content
) {
}
