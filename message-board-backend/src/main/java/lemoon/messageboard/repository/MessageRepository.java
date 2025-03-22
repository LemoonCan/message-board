package lemoon.messageboard.repository;

import lemoon.messageboard.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m JOIN Customer c ON m.customer.id = c.id WHERE c.id = :customerId ORDER BY m.createdTime DESC")
    List<Message> findByCustomerId(@Param("customerId") Long customerId);
} 