package com.vryez.backendlab.lab27;

public record VideoUploadedEvent(long videoId, String uploaderId, String title) {
}
