package com.vryez.backendlab.lab17;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserContextFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository;

    public UserContextFilter(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("X-User-Id");
        if (header != null && !header.isBlank()) {
            Long userId = Long.valueOf(header);
            memberRepository.findNicknameById(userId)
                    .ifPresent(nick -> UserContextHolder.set(new UserContext(userId, nick)));
        }
        try {
            chain.doFilter(req, res);
        } finally {
            UserContextHolder.clear();
        }
    }
}
