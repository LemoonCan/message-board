package lemoon.messageboard.application.service;

import lemoon.messageboard.application.dto.CustomerDTO;
import lemoon.messageboard.application.dto.RegisterParam;
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
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterParam registerParam) {
        // 检查用户名是否已存在
        if (customerRepository.existsByName(registerParam.getName())) {
            throw new RuntimeException("用户名已被使用");
        }

        // 检查邮箱是否已存在
        if (customerRepository.existsByEmail(registerParam.getEmail())) {
            throw new RuntimeException("邮箱已被使用");
        }

        // 创建用户并保存
        Customer customer = new Customer();
        customer.setName(registerParam.getName());
        customer.setEmail(registerParam.getEmail());
        customer.setPassword(passwordEncoder.encode(registerParam.getPassword()));
        customer.setCreatedAt(LocalDateTime.now());

        customerRepository.save(customer);
    }

    @Transactional
    public void updateLastLoginDate(String username) {
        Optional<Customer> userOptional = customerRepository.findByName(username);
        if (userOptional.isPresent()) {
            Customer customer = userOptional.get();
            customer.setLastLoginAt(LocalDateTime.now());
            customerRepository.save(customer);
        }
    }

    @Transactional
    public CustomerDTO findUser(String name) {
        Optional<Customer> userOptional = customerRepository.findByName(name);
        if (userOptional.isPresent()) {
            Customer customer = userOptional.get();
            return new CustomerDTO(customer.getId(), customer.getName(), customer.getEmail());
        }
        return null;
    }
} 