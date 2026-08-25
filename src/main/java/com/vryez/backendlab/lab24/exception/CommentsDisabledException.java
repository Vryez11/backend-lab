package com.vryez.backendlab.lab24.exception;

public class CommentsDisabledException extends RuntimeException {

    public CommentsDisabledException(Long videoId) {
        super("댓글이 비활성화된 영상입니다: " + videoId);
    }
}
