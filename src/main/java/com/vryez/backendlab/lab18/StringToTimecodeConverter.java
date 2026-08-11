package com.vryez.backendlab.lab18;

import org.springframework.core.convert.converter.Converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringToTimecodeConverter implements Converter<String, Timecode> {

    private static final Pattern TIMECODE = Pattern.compile("(\\d+):([0-5]\\d)");

    @Override
    public Timecode convert(String source) {
        Matcher m = TIMECODE.matcher(source);
        if (!m.matches()) {
            throw new IllegalArgumentException(
                    "타임코드는 '분:초' 형식이어야 합니다 (초는 00~59): " + source);
        }
        int minutes = Integer.parseInt(m.group(1));
        int seconds = Integer.parseInt(m.group(2));
        return new Timecode(minutes * 60 + seconds);
    }
}
