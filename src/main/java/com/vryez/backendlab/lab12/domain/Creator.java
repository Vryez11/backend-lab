package com.vryez.backendlab.lab12.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Creator {

    private final Long id;
    private final String name;
    private final long payoutBalance;
}
