package com.vryez.backendlab.lab17;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyPageController {

    @GetMapping("/api/lab/me")
    public ResponseEntity<?> me() {

        long userid;
        String nickname;

        UserContext ctx = UserContextHolder.get();
        if (ctx == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "unauthenticated"));
        }
        userid = ctx.userId();
        nickname = ctx.nickname();


        return ResponseEntity.ok(Map.of("userId", userid, "nickname", nickname));
    }
}
