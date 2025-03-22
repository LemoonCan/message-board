package lemoon.messageboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lemoon
 * @since 2025/3/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO implements Serializable {
    private Long id;
    private String name;
    private String email;
}
