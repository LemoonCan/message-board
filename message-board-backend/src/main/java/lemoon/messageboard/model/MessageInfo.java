package lemoon.messageboard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author lemoon
 * @since 2025/3/27
 */
@Data
@Entity
public class MessageInfo {
    @Id
    private Long id;
    @Column
    private String content;
    @Column
    private String customerName;
    @Column
    private LocalDateTime createdAt;
    @Column
    private Long parentId;
}
