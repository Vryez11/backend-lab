package com.vryez.backendlab.lab20;

public class VideoNotFoundException extends RuntimeException {

    public VideoNotFoundException(Long id) {
        super("video not found: " + id);
    }
}
