package com.vryez.backendlab.lab13;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record GiftRequest(
        @NotBlank String viewerName,
        @Positive long amount
) {
}
