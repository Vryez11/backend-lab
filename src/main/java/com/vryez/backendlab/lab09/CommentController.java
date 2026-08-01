package com.vryez.backendlab.lab09;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/videos/{videoId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;

    // TODO(과제): 댓글 목록 조회.
    //             경로의 videoId와 쿼리 파라미터 page·size·sort·keyword를 받아
    //             CommentPageResponse를 반환하라. 파라미터가 하나도 없어도 동작해야 한다.
    //             (기본값: page=0, size=10, sort=latest / keyword는 선택)
    @GetMapping
    public CommentPageResponse list(
            @PathVariable Long videoId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(0) int size,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) String keyword
    ) {

        // 거르기 → 정렬까지 마친 전체 목록. 이 크기가 totalCount다(슬라이스 전에 확정).
        List<Comment> filtered = commentRepository.findByVideoId(videoId).stream()
                .filter(c -> keyword == null || c.getContent().contains(keyword))
                .sorted("oldest".equals(sort)
                        ? Comparator.comparing(Comment::getCreatedAt)
                        : Comparator.comparing(Comment::getCreatedAt).reversed())
                .toList();

        // skip=시작 인덱스, limit=남길 개수. 범위를 벗어나면 예외 없이 빈 목록이 된다.
        List<CommentResponse> pageItems = filtered.stream()
                .skip((long) page * size)
                .limit(size)
                .map(CommentResponse::from)
                .toList();

        return new CommentPageResponse(page, size, sort, filtered.size(), pageItems);
    }

    // TODO(과제): 댓글 등록.
    //             폼(form-urlencoded) 파라미터 author·content를 받아 저장하고
    //             201과 함께 생성된 댓글을 반환하라.
    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @ModelAttribute CommentCreateRequest request,
            @PathVariable Long videoId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommentResponse.from(
                        commentRepository.save(new Comment(null, videoId, request.getAuthor(), request.getContent(), LocalDateTime.now()))));
    }
}
