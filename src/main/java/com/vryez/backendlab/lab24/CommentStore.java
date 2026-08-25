package com.vryez.backendlab.lab24;

import com.vryez.backendlab.lab24.exception.VideoNotFoundException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CommentStore {

    private final Map<Long, Video> videos = new ConcurrentHashMap<>();
    private final Map<Long, Comment> comments = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong();

    public CommentStore() {
        videos.put(1L, new Video(1L, "스프링 입문", true));
        videos.put(2L, new Video(2L, "JPA 기본", true));
        videos.put(3L, new Video(3L, "비공개 사내 교육본", false));
    }

    public Video getVideoOrThrow(Long id) {
        Video video = videos.get(id);
        if (video == null) {
            throw new VideoNotFoundException(id);
        }
        return video;
    }

    public Comment save(Long videoId, String author, String content) {
        long id = seq.incrementAndGet();
        Comment comment = new Comment(id, videoId, author, content, LocalDateTime.now());
        comments.put(id, comment);
        return comment;
    }

    public List<Comment> findByVideo(Long videoId) {
        return comments.values().stream()
                .filter(comment -> comment.videoId().equals(videoId))
                .toList();
    }
}
