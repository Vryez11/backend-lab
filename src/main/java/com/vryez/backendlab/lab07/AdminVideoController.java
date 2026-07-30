package com.vryez.backendlab.lab07;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("lab07AdminVideoController")
@RequestMapping("/lab07/admin")
public class AdminVideoController {

    private final VideoRepository videoRepository;

    public AdminVideoController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    // 권한 검증은 공통 관심사로 인터셉터에 위임한다
    @DeleteMapping("/videos/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (videoRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        videoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
