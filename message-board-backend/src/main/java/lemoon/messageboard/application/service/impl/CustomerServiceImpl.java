package lemoon.messageboard.application.service.impl;

import lemoon.messageboard.application.dto.CustomerDTO;
import lemoon.messageboard.application.dto.LoginDTO;
import lemoon.messageboard.application.dto.LoginParam;
import lemoon.messageboard.application.dto.RegisterParam;
import lemoon.messageboard.application.service.CustomerService;
import lemoon.messageboard.config.security.jwt.JwtTokenProvider;
import lemoon.messageboard.model.Customer;
import lemoon.messageboard.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author lemoon
 * @since 2025/3/21
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    @Override
    public void register(RegisterParam registerParam) {
        if (customerRepository.existsByName(registerParam.getName())) {
            throw new RuntimeException("用户名重复");
        }
        if (customerRepository.existsByEmail(registerParam.getEmail())) {
            throw new RuntimeException("邮箱重复");
        }

        Customer customer = new Customer();
        customer.setName(registerParam.getName());
        customer.setEmail(registerParam.getEmail());
        customer.setPassword(passwordEncoder.encode(registerParam.getPassword()));
        customer.setCreatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Override
    public LoginDTO login(LoginParam loginParam) {
        Optional<Customer> customerOptional = customerRepository.findByName(loginParam.getIdentity());
        if (customerOptional.isEmpty()) {
            customerOptional = customerRepository.findByEmail(loginParam.getIdentity());
        }
        if (customerOptional.isEmpty()) {
            throw new RuntimeException("用户不存在,请检查用户名或邮箱是否正确");
        }
        Customer customer = customerOptional.get();

        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(customer.getName(),
                        loginParam.getPassword())
        );

        // 更新最后登录时间
        customer.setLastLoginAt(LocalDateTime.now());
        customerRepository.save(customer);

        // 根据是否勾选"记住我"生成不同有效期的JWT令牌
        boolean rememberMe = loginParam.getRememberMe() != null && loginParam.getRememberMe();
        String jwt = jwtTokenProvider.createToken(authentication, rememberMe);
        return new LoginDTO(jwt,
                new CustomerDTO(customer.getName(), customer.getEmail()));
    }
} 