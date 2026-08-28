package com.vryez.backendlab.lab27;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class VideoUploadRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String uploaderId;
}
