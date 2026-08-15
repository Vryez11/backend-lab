package com.vryez.backendlab.lab20;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.DispatcherType;

@Configuration
public class Lab20Config {

    @Bean
    public FilterRegistrationBean<RequestAuditFilter> lab20RequestAuditFilter() {
        FilterRegistrationBean<RequestAuditFilter> registration =
                new FilterRegistrationBean<>(new RequestAuditFilter());
        registration.addUrlPatterns("/lab20/*");
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        return registration;
    }
}
