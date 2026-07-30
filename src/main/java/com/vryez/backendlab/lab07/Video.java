package com.vryez.backendlab.lab07;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Video {

    private Long id;
    private String title;
    private String uploaderLoginId;
    private long viewCount;
}
