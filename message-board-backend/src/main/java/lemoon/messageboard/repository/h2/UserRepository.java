package lemoon.messageboard.repository.h2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lee
 * @since 2025/3/20
 */
@Repository
public interface UserRepository extends JpaRepository<Customer, Long> {
}