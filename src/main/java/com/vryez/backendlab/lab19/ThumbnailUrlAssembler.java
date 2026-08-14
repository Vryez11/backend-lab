package com.vryez.backendlab.lab19;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ThumbnailUrlAssembler {

    @Value("${app.cdn-base-urlㅂ}")
    private String cdnBaseUrl;

    private String prefix;

    public ThumbnailUrlAssembler() {
    }

    @PostConstruct
    public void init() {
        this.prefix = cdnBaseUrl + "/th/";
    }

    public String assemble(String thumbnailKey) {
        return prefix + thumbnailKey + ".jpg";
    }
}
