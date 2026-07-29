package com.vryez.backendlab.lab06.domain;

import lombok.Getter;

@Getter
public class UserPoint {

    private final String userId;
    private final long point;
    private final boolean giftEnabled;

    public UserPoint(String userId, long point, boolean giftEnabled) {
        this.userId = userId;
        this.point = point;
        this.giftEnabled = giftEnabled;
    }
}
