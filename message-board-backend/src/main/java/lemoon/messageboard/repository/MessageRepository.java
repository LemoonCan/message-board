package lemoon.messageboard.repository;

import lemoon.messageboard.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author lemoon
 * @since 2025/3/24
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m ORDER BY m.id DESC")
    List<Message> findAllByIdAtDesc();
}
