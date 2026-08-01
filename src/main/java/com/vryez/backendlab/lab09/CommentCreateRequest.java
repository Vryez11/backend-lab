package com.vryez.backendlab.lab09;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateRequest {

    private String author;
    private String content;
}
