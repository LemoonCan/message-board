package lemoon.messageboard.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lemoon
 * @since 2025/3/21
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterParam {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 5, max = 20, message = "用户名长度需在4到20字符之间")
    @Pattern(
            regexp = "^[A-Za-z0-9]+$",
            message = "账号只能包含字母和数字"
    )
    private String name;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度需在6到20字符之间")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).+$",
            message = "密码必须包含至少1个大写字母、1个小写字母、1个数字和1个特殊符号(!@#$%^&*)"
    )
    private String password;
} 