package com.vryez.backendlab.lab27;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VideoUploadServiceTest {

    @Autowired
    private VideoUploadService videoUploadService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void title이_넘치더라도_upload는_가능하다() {

        VideoUploadRequest request = new VideoUploadRequest();

        String longTitle = "이건 영국에서 시작해서 바다를 건너, 한국까지 온 대단한 여행일지! 이것을 본다면 ... [더보기]";
        request.setTitle(longTitle);
        request.setUploaderId("나다.");

        long videoId = videoUploadService.upload(request);

        // 반환값이 아니라 DB를 재조회해서 실제 저장 결과를 단언한다.
        // (auto_increment는 실행 순서에 따라 달라지므로 고정 ID 비교는 하지 않는다.)
        String savedTitle = jdbcTemplate.queryForObject(
                "select title from lab27_video where id = ?", String.class, videoId);
        assertThat(savedTitle).isEqualTo(longTitle);

        Integer statCount = jdbcTemplate.queryForObject(
                "select count(*) from lab27_video_stat where video_id = ?", Integer.class, videoId);
        assertThat(statCount).isEqualTo(1);
    }
}
