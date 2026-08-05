package com.vryez.backendlab.lab12.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Payout {

    private final Long id;
    private final Long creatorId;
    private final long amount;
    private final String status;
}
