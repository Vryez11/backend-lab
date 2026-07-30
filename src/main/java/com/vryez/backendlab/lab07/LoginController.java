package com.vryez.backendlab.lab07;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("lab07LoginController")
@RequestMapping("/lab07")
public class LoginController {

    private final MemberRepository memberRepository;

    public LoginController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    record LoginRequest(String loginId, String password) {
    }

    record LoginResponse(String loginId, String name, Role role) {
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                               HttpServletRequest request) {
        return memberRepository
                .findByLoginIdAndPassword(loginRequest.loginId(), loginRequest.password())
                .map(member -> {
                    HttpSession session = request.getSession(true);
                    session.setAttribute(SessionConst.LOGIN_MEMBER, member);
                    return ResponseEntity.ok(
                            new LoginResponse(member.getLoginId(), member.getName(), member.getRole()));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().build();
    }
}
