package com.vryez.backendlab.lab05;

import com.vryez.backendlab.lab05.videoview.VideoViewLabConfig;
import com.vryez.backendlab.lab05.videoview.VideoViewRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class ViewCount10IncreaseTest {

    private DataSource dataSource = new VideoViewLabConfig().videoViewDataSource();
    private VideoViewRepository repository = new VideoViewRepository(dataSource);

    @Test
    void 뷰_카운트를_연속_10번_했을_때() {

        for (int i = 0; i < 10; i++) {
            repository.increaseViewCount("v1");
        }

        assertThat(repository.getViewCount("v1")).isSameAs(10L);
    }

}
