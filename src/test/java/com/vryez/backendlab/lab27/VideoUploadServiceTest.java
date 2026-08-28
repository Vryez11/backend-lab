package com.vryez.backendlab.lab27;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VideoUploadServiceTest {

    @Autowired
    private VideoUploadService videoUploadService;

    @Test
    void title이_넘치더라도_upload는_가능하다(){

        VideoUploadRequest request = new VideoUploadRequest();

        request.setTitle("이건 영국에서 시작해서 바다를 건너, 한국까지 온 대단한 여행일지! 이것을 본다면 ... [더보기]");
        request.setUploaderId("나다.");

        long videoId = videoUploadService.upload(request);

        Assertions.assertThat(videoId).isEqualTo(1L);
    }
}