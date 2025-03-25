package lemoon.messageboard.application.service;

import lemoon.messageboard.application.dto.CustomerDTO;
import lemoon.messageboard.application.dto.LoginDTO;
import lemoon.messageboard.application.dto.LoginParam;
import lemoon.messageboard.application.dto.RegisterParam;

/**
 * @author lemoon
 * @since 2025/3/21
 */
public interface CustomerService {
    /**
     * 注册
     * @param registerParam 注册参数
     */
    void register(RegisterParam registerParam);

    /**
     * 登录
     * @param loginParam 登录参数
     * @return 登录信息
     */

    LoginDTO login(LoginParam loginParam);
}
