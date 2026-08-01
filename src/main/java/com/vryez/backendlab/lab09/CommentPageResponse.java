package com.vryez.backendlab.lab09;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CommentPageResponse {

    private int page;
    private int size;
    private String sort;
    private long totalCount;
    private List<CommentResponse> comments;
}
