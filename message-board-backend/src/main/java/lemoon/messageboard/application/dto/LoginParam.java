package lemoon.messageboard.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginParam {
    @NotBlank(message = "用户名或邮箱不能为空")
    private String identity;

    @NotBlank(message = "密码不能为空")
    private String password;
    
    private Boolean rememberMe = false;
} 