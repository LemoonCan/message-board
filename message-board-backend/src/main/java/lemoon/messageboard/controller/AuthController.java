package lemoon.messageboard.controller;

import jakarta.validation.Valid;
import lemoon.messageboard.application.dto.CustomerDTO;
import lemoon.messageboard.application.dto.LoginDTO;
import lemoon.messageboard.application.dto.LoginParam;
import lemoon.messageboard.application.dto.RegisterParam;
import lemoon.messageboard.application.service.CustomerService;
import lemoon.messageboard.config.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterParam registerParam) {
        customerService.register(registerParam);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerDTO> login(@RequestBody @Valid LoginParam loginParam) {
        LoginDTO loginDTO = customerService.login(loginParam);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginDTO.getToken())
                .body(loginDTO.getCustomerDTO());
    }
}