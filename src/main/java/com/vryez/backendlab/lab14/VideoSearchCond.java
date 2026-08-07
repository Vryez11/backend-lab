package com.vryez.backendlab.lab14;

import lombok.Data;

/**
 * 목록 조회 조건. 파라미터가 생략되면 아래 기본값이 그대로 남는다.
 */
@Data
public class VideoSearchCond {

    private int page = 0;
    private int size = 10;
    private String sort = "latest";
}
