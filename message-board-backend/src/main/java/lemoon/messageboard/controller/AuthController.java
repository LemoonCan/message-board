package lemoon.messageboard.controller;

import jakarta.validation.Valid;
import lemoon.messageboard.application.dto.LoginParam;
import lemoon.messageboard.application.dto.RegisterParam;
import lemoon.messageboard.config.security.jwt.JwtTokenProvider;
import lemoon.messageboard.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterParam registerParam) {
        userService.register(registerParam);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginParam loginParam) {
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginParam.getName(),
                        loginParam.getPassword()
                )
        );

        // 生成JWT令牌
        String jwt = jwtTokenProvider.createToken(authentication);
        // 更新最后登录时间
        userService.updateLastLoginDate(loginParam.getName());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .build();
    }
}