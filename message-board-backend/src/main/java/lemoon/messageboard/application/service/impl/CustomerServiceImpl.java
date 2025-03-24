package lemoon.messageboard.application.service.impl;

import lemoon.messageboard.application.dto.CustomerDTO;
import lemoon.messageboard.application.dto.RegisterParam;
import lemoon.messageboard.application.service.CustomerService;
import lemoon.messageboard.model.Customer;
import lemoon.messageboard.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    @Override
    public void register(RegisterParam registerParam) {
        if (customerRepository.existsByName(registerParam.getName())) {
            throw new RuntimeException("用户名已被使用");
        }

        if (customerRepository.existsByEmail(registerParam.getEmail())) {
            throw new RuntimeException("邮箱已被使用");
        }

        Customer customer = new Customer();
        customer.setName(registerParam.getName());
        customer.setEmail(registerParam.getEmail());
        customer.setPassword(passwordEncoder.encode(registerParam.getPassword()));
        customer.setCreatedAt(LocalDateTime.now());

        customerRepository.save(customer);
    }

    @Transactional
    @Override
    public void updateLastLoginDate(String username) {
        Optional<Customer> userOptional = customerRepository.findByName(username);
        if (userOptional.isPresent()) {
            Customer customer = userOptional.get();
            customer.setLastLoginAt(LocalDateTime.now());
            customerRepository.save(customer);
        }
    }

    @Override
    public CustomerDTO findUser(String name) {
        Optional<Customer> userOptional = customerRepository.findByName(name);
        if (userOptional.isPresent()) {
            Customer customer = userOptional.get();
            return new CustomerDTO(customer.getId(), customer.getName(), customer.getEmail());
        }
        return null;
    }
} 