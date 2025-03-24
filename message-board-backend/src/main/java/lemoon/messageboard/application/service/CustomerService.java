package lemoon.messageboard.application.service;

import lemoon.messageboard.application.dto.CustomerDTO;
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
     * 更新最后登录时间
     * @param name 用户名
     */
    void updateLastLoginDate(String name);

    /**
     * 查找用户
     * @param name 用户名
     * @return
     */
    CustomerDTO findUser(String name);
}
