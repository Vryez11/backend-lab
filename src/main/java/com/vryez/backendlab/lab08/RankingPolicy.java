package com.vryez.backendlab.lab08;

import java.util.List;

public interface RankingPolicy {
    List<Video> rank(List<Video> videos, int limit);
}
