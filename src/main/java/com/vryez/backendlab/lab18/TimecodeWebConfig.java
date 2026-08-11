package com.vryez.backendlab.lab18;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TimecodeWebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // TODO: 직접 만든 StringToTimecodeConverter를 여기서 등록하라.

        registry.addConverter(new StringToTimecodeConverter());
    }
}
