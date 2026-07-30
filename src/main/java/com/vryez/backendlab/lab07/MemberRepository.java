package com.vryez.backendlab.lab07;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository("lab07MemberRepository")
public class MemberRepository {

    private final Map<String, Member> store = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        store.put("admin", new Member(1L, "admin", "admin1234", "관리자", Role.ADMIN));
        store.put("viewer", new Member(2L, "viewer", "viewer1234", "일반회원", Role.USER));
    }

    public Optional<Member> findByLoginIdAndPassword(String loginId, String password) {
        return findByLoginId(loginId)
                .filter(member -> member.getPassword().equals(password));
    }

    public Optional<Member> findByLoginId(String loginId) {
        return Optional.ofNullable(store.get(loginId));
    }
}
