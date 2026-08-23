package com.vryez.backendlab.lab23;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoCreateRequest {

    @NotBlank(message = "제목은 비어 있을 수 없습니다.")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    private String title;

    @NotNull(message = "재생시간은 필수입니다.")
    @Positive(message = "재생시간은 0보다 커야 합니다.")
    private Integer durationSeconds;

    @NotNull(message = "공개 여부는 필수입니다.")
    private Boolean isPublic;
}
