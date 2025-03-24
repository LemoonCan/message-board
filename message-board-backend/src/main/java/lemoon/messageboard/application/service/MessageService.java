package lemoon.messageboard.application.service;

import lemoon.messageboard.application.dto.MessageDTO;

import java.util.List;

/**
 * @author lemoon
 * @since 2025/3/24
 */
public interface MessageService {
    /**
     * 创建根留言
     * @param messageDTO 留言内容
     */
    MessageDTO createRootMessage(MessageDTO messageDTO);

    /**
     * 回复留言
     * @param parentId 父留言ID
     * @param messageDTO 留言内容
     * @return
     */
    MessageDTO replyToMessage(Long parentId, MessageDTO messageDTO);

    /**
     * 获取所有留言
     * @return 留言树
     */
    List<MessageDTO> getFullMessageTree();

}
