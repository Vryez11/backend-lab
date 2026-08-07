package com.vryez.backendlab.lab14;

import java.util.List;

public record VideoPageResponse(List<VideoResponse> content, int page, int size, long totalCount) {
}
