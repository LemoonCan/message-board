package lemoon.messageboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author lemoon
 * @since 2025/3/25
 */
@Data
@AllArgsConstructor
public class LoginDTO {
    private String token;
    private CustomerDTO customerDTO;
}
