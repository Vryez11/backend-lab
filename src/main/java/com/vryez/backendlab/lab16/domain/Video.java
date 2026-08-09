package com.vryez.backendlab.lab16.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Video {

    private Long id;
    private String title;
    private long viewCount;
    private boolean reported;
    private String moderationReason;
}
