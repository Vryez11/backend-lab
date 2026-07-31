package com.vryez.backendlab.lab08;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Video {
    private final Long id;
    private final String title;
    private final Long uploaderId;
    private final long viewCount;
}
