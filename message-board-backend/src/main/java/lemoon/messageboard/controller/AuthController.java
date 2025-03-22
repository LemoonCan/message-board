package lemoon.messageboard.controller;

import jakarta.validation.Valid;
import lemoon.messageboard.application.dto.CustomerDTO;
import lemoon.messageboard.application.dto.LoginParam;
import lemoon.messageboard.application.dto.RegisterParam;
import lemoon.messageboard.application.service.CustomerService;
import lemoon.messageboard.config.security.jwt.JwtTokenProvider;
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
    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterParam registerParam) {
        customerService.register(registerParam);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerDTO> login(@RequestBody @Valid LoginParam loginParam) {
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginParam.getName(),
                        loginParam.getPassword()
                )
        );

        // 根据是否勾选"记住我"生成不同有效期的JWT令牌
        boolean rememberMe = loginParam.getRememberMe() != null && loginParam.getRememberMe();
        String jwt = jwtTokenProvider.createToken(authentication, rememberMe);
        
        // 更新最后登录时间
        customerService.updateLastLoginDate(loginParam.getName());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .body(customerService.findUser(loginParam.getName()));
    }
}