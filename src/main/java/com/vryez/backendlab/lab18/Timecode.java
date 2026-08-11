package com.vryez.backendlab.lab18;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Timecode {
    private final int totalSeconds;

    public Timecode(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }
}
