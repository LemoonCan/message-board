package lemoon.messageboard.controller;
import org.springframework.web.bind.annotation.*;
import lemoon.messageboard.repository.h2.Customer;
import lemoon.messageboard.repository.h2.UserRepository;

import java.util.List;
/**
 * @author lee
 * @since 2025/3/20
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/getAllUsers")
    public List<Customer> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/createUser")
    public Customer createUser(@RequestBody Customer customer) {
        return userRepository.save(customer);
    }

    @GetMapping("/getUserById")
    public Customer getUserById(@RequestParam("id") Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam("id") Long id){
        userRepository.deleteById(id);
    }
}
