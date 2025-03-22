package lemoon.messageboard.repository;

import lemoon.messageboard.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByName(String name);
    Optional<Customer> findByEmail(String email);
    boolean existsByName(String name);
    boolean existsByEmail(String email);
} 