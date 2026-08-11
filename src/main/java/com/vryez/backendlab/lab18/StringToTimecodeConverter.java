package com.vryez.backendlab.lab18;

import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class StringToTimecodeConverter implements Converter<String, Timecode> {
    @Override
    public Timecode convert(String source) {
        // TODO: "분:초" 문자열을 Timecode로 변환하라.
        // 형식: 분(1자리 이상 정수) ":" 초(정확히 2자리, 00~59)
        // 규칙 위반 시 IllegalArgumentException을 던져라.

        String[] split = source.trim().split(":");

        if (split.length != 2) {
            throw new IllegalArgumentException("올바른 형태의 요청이 아닙니다.");
        } else if (split[1].length() != 2) {
            throw  new IllegalArgumentException("올바른 형태의 요청이 아닙니다.");
        }

        int min, sec;

        try {
            min = Integer.parseInt(split[0]);
            sec = Integer.parseInt(split[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("올바른 형태의 요청이 아닙니다.");
        }

        if (min < 0) {
            throw new IllegalArgumentException("올바른 형태의 요청이 아닙니다.");
        } else if (sec < 0 || sec > 59) {
            throw new IllegalArgumentException("올바른 형태의 요청이 아닙니다.");
        }

        return new Timecode(min * 60 + sec);
    }
}
