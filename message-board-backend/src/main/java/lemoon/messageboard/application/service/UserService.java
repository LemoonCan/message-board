package lemoon.messageboard.application.service;

import lemoon.messageboard.application.dto.RegisterRequest;
import lemoon.messageboard.model.Customer;
import lemoon.messageboard.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (customerRepository.existsByName(registerRequest.getName())) {
            throw new RuntimeException("用户名已被使用");
        }

        // 检查邮箱是否已存在
        if (customerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("邮箱已被使用");
        }

        // 创建用户并保存
        Customer customer = new Customer();
        customer.setName(registerRequest.getName());
        customer.setEmail(registerRequest.getEmail());
        customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        customer.setCreatedTime(LocalDateTime.now());

        customerRepository.save(customer);
    }

    @Transactional
    public void updateLastLoginDate(String username) {
        Optional<Customer> userOptional = customerRepository.findByName(username);
        if (userOptional.isPresent()) {
            Customer customer = userOptional.get();
            customer.setLastLoginTime(LocalDateTime.now());
            customerRepository.save(customer);
        }
    }
} 