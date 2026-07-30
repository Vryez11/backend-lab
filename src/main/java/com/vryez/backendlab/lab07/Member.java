package com.vryez.backendlab.lab07;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Member {

    private Long id;
    private String loginId;
    private String password;
    private String name;
    private Role role;
}
