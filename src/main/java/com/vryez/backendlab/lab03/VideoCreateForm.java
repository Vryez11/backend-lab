package com.vryez.backendlab.lab03;

import lombok.Data;

@Data // @ModelAttribute의 setter 바인딩 대상
public class VideoCreateForm {
    private String title;
    private String uploader;
    private int durationSec;
}
