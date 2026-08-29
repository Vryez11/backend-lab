package com.vryez.backendlab.lab28;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 주의: 테스트 자체에 @Transactional을 붙이지 않는다 — 실제 커밋/롤백 여부를 검증해야 한다.
@SpringBootTest
@Sql({"/lab28/schema.sql", "/lab28/data.sql"})
class VideoTransferServiceTest {

    @Autowired
    VideoTransferService videoTransferService;

    @Autowired
    VideoTransferRepository repository;

    @Test
    @DisplayName("잠긴 영상이 없는 목록은 전부 이관된다 — 소속 채널, 채널별 영상 수, 이관 로그까지")
    void 정상_이관() throws Exception {
        videoTransferService.transferAll(1L, 2L, List.of(101L, 102L));

        assertThat(repository.findChannelId(101L)).isEqualTo(2L);
        assertThat(repository.findChannelId(102L)).isEqualTo(2L);
        assertThat(repository.findVideoCount(1L)).isEqualTo(3);
        assertThat(repository.findVideoCount(2L)).isEqualTo(2);
        assertThat(repository.countTransferLogs()).isEqualTo(2);
    }

    @Test
    @DisplayName("잠긴 영상(103)이 섞이면 예외가 전달되고, 그 배치의 어떤 영상도 이관되지 않는다(완전 원복)")
    void 잠긴영상_포함시_전체취소() {
        assertThatThrownBy(() ->
                videoTransferService.transferAll(1L, 2L, List.of(101L, 102L, 103L, 104L)))
                .isInstanceOf(LockedVideoException.class);

        assertThat(repository.findChannelId(101L)).isEqualTo(1L);
        assertThat(repository.findChannelId(102L)).isEqualTo(1L);
        assertThat(repository.findChannelId(104L)).isEqualTo(1L);
        assertThat(repository.findVideoCount(1L)).isEqualTo(5);
        assertThat(repository.findVideoCount(2L)).isEqualTo(0);
        assertThat(repository.countTransferLogs()).isZero();
    }
}
