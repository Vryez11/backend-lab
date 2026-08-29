package com.vryez.backendlab.lab28;

public class LockedVideoException extends Exception {

    public LockedVideoException(long videoId) {
        super("잠긴 영상은 이관할 수 없습니다: videoId=" + videoId);
    }
}
