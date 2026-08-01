package com.vryez.backendlab.lab09;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Comment {

    private Long id;
    private Long videoId;
    private String author;
    private String content;
    private LocalDateTime createdAt;
}
