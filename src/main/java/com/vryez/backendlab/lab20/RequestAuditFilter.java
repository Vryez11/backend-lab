package com.vryez.backendlab.lab20;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 요청 시작 시 X-Admin-Id 헤더를 읽어 관리자 컨텍스트를 채우고,
 * 요청이 끝나면 컨텍스트를 정리한다.
 */
public class RequestAuditFilter implements Filter {

    public static final String ADMIN_HEADER = "X-Admin-Id";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String adminId = request.getHeader(ADMIN_HEADER);
        if (adminId != null && !adminId.isBlank()) {
            AdminContextHolder.set(adminId);
        }
        try {
            chain.doFilter(req, res);
        } finally {
            AdminContextHolder.clear();
        }
    }
}
