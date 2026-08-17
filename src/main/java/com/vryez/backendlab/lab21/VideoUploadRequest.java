package com.vryez.backendlab.lab21;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class VideoUploadRequest {

    @NotBlank
    @Size(max = 100)
    private String title;

    @Pattern(regexp = "^(?=.{3,30}$)[a-z0-9]+(?:-[a-z0-9]+)*$")
    private String handle;

    @Pattern(regexp = "^(GAME|MUSIC|VLOG|EDU|SPORTS)$")
    private String category;

    @NotNull
    @Max(43200)
    @Min(1)
    private Integer durationSec;
}
