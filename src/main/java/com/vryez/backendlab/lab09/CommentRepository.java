package com.vryez.backendlab.lab09;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CommentRepository {

    private final Map<Long, Comment> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong();

    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment = new Comment(seq.incrementAndGet(), comment.getVideoId(),
                    comment.getAuthor(), comment.getContent(), comment.getCreatedAt());
        }
        store.put(comment.getId(), comment);
        return comment;
    }

    public List<Comment> findByVideoId(Long videoId) {
        return store.values().stream()
                .filter(c -> c.getVideoId().equals(videoId))
                .sorted(Comparator.comparing(Comment::getId))
                .toList();
    }

    @PostConstruct
    void seed() {
        save(new Comment(null, 1L, "kim", "첫 댓글이에요", LocalDateTime.of(2026, 7, 20, 9, 0)));
        save(new Comment(null, 1L, "lee", "이 부분 질문 있습니다", LocalDateTime.of(2026, 7, 20, 9, 5)));
        save(new Comment(null, 1L, "park", "영상 잘 봤습니다", LocalDateTime.of(2026, 7, 20, 9, 10)));
        save(new Comment(null, 1L, "choi", "버그 재현 방법이 궁금해요", LocalDateTime.of(2026, 7, 20, 9, 15)));
        save(new Comment(null, 1L, "jung", "질문: 트랜잭션은 어디서 시작되나요", LocalDateTime.of(2026, 7, 20, 9, 20)));
        save(new Comment(null, 1L, "yoon", "감사합니다 도움이 됐어요", LocalDateTime.of(2026, 7, 20, 9, 25)));
        save(new Comment(null, 1L, "han", "여기서 버그가 나는 것 같아요", LocalDateTime.of(2026, 7, 20, 9, 30)));
        save(new Comment(null, 1L, "seo", "다음 편도 기대할게요", LocalDateTime.of(2026, 7, 20, 9, 35)));
        save(new Comment(null, 1L, "oh", "질문 하나 더 드려도 될까요", LocalDateTime.of(2026, 7, 20, 9, 40)));
        save(new Comment(null, 1L, "shin", "설명이 명확해서 좋네요", LocalDateTime.of(2026, 7, 20, 9, 45)));
        save(new Comment(null, 1L, "kang", "실습 코드 어디서 받나요", LocalDateTime.of(2026, 7, 20, 9, 50)));
        save(new Comment(null, 1L, "cho", "잘 정리된 강의네요", LocalDateTime.of(2026, 7, 20, 9, 55)));
    }
}
