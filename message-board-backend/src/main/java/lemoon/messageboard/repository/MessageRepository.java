package lemoon.messageboard.repository;

import lemoon.messageboard.model.Message;
import lemoon.messageboard.model.MessageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author lemoon
 * @since 2025/3/24
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value = "SELECT c.NAME as customer_name,m.* FROM MESSAGE m " +
                    "LEFT JOIN CUSTOMER c on c.ID = m.CUSTOMER_ID " +
                    "ORDER BY m.ID DESC",
            nativeQuery = true)
    List<MessageInfo> findAllByIdAtDesc();
}
