package com.vryez.backendlab.lab14;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class VideoListService {

    private final VideoRepository repository;

    public VideoListService(VideoRepository repository) {
        this.repository = repository;
    }

    public VideoPageResponse list(int page, int size, String sort) {
        // TODO: 학생 구현 — repository.findAll()을 sort 기준으로 정렬한 뒤,

        List<Video> sorted = repository.findAll().stream()
                .sorted(sort.equals("oldest")
                        ? Comparator.comparing(Video::createdAt):sort.equals("latest")
                                                                 ? Comparator.comparing(Video::createdAt).reversed()
                                                                 : Comparator.comparing(Video::views).reversed()) // 가장 많은 뷰어쉽
                .toList();

        List<VideoResponse> sliced = sorted.stream()
                .skip((long) page * size)
                .limit(size)
                .map(VideoResponse::from)
                .toList();

        //  page/size로 슬라이스하고, totalCount는 슬라이스 전 전체 개수로 채운다.
        return new VideoPageResponse(sliced, page, size, sorted.size()); // 스텁: 반드시 교체
    }
}
