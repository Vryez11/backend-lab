package com.vryez.backendlab.lab24.web;

import com.vryez.backendlab.lab24.Comment;
import com.vryez.backendlab.lab24.CommentStore;
import com.vryez.backendlab.lab24.Video;
import com.vryez.backendlab.lab24.exception.CommentsDisabledException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("lab24CommentController")
@RequestMapping("/lab24/videos/{videoId}/comments")
public class CommentController {

    private final CommentStore store;

    public CommentController(CommentStore store) {
        this.store = store;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long videoId,
            @RequestBody @Valid CommentCreateRequest request) {
        Video video = store.getVideoOrThrow(videoId);
        if (!video.commentsEnabled()) {
            throw new CommentsDisabledException(videoId);
        }
        Comment saved = store.save(videoId, request.author(), request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.from(saved));
    }

    @GetMapping
    public List<CommentResponse> list(@PathVariable Long videoId) {
        store.getVideoOrThrow(videoId);
        return store.findByVideo(videoId).stream().map(CommentResponse::from).toList();
    }
}
