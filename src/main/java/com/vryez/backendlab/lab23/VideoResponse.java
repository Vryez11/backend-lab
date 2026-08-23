package com.vryez.backendlab.lab23;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoResponse {
    private Long id;
    private String title;
    private Integer durationSeconds;
}
