package com.vryez.backendlab.lab20;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 요청 하나가 어떻게 끝나든, 그 요청을 처리한 스레드에
 * 관리자 컨텍스트가 남아 있으면 안 된다.
 */
class RequestAuditFilterTest {

    final RequestAuditFilter filter = new RequestAuditFilter();

    @AfterEach
    void tearDown() {
        AdminContextHolder.clear();
    }

    @Test
    void 정상_처리된_요청_뒤에는_컨텍스트가_남지_않는다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestAuditFilter.ADMIN_HEADER, "admin-1");
        FilterChain okChain = (req, res) -> { };

        filter.doFilter(request, new MockHttpServletResponse(), okChain);

        assertThat(AdminContextHolder.get()).isNull();
    }

    @Test
    void 처리_중_예외가_난_요청_뒤에도_컨텍스트가_남지_않는다() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestAuditFilter.ADMIN_HEADER, "admin-2");
        FilterChain boomChain = (req, res) -> {
            throw new ServletException("영상 삭제 처리 중 폭발");
        };

        assertThatThrownBy(() -> filter.doFilter(request, new MockHttpServletResponse(), boomChain))
                .isInstanceOf(ServletException.class);

        assertThat(AdminContextHolder.get()).isNull();
    }
}
