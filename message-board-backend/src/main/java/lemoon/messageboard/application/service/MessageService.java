package lemoon.messageboard.application.service;

import lemoon.messageboard.application.dto.MessageDTO;

import java.util.List;

public interface MessageService {
    
    // 创建顶级留言
    MessageDTO createRootMessage(MessageDTO messageDTO);
    
    // 回复留言（创建子留言）
    MessageDTO replyToMessage(Long parentId, MessageDTO messageDTO);
    
    // 获取所有顶级留言
    List<MessageDTO> getAllRootMessages();
    
    // 获取指定消息的所有子留言（一级子留言）
    List<MessageDTO> getChildrenMessages(Long messageId);
    
    // 获取消息树，最大深度为maxDepth（从顶级留言算起）
    List<MessageDTO> getMessageTree(Integer maxDepth);
    
    // 获取指定留言的消息树，最大深度为maxDepth
    MessageDTO getMessageTreeById(Long messageId, Integer maxDepth);
    
    // 获取完整的消息树（无深度限制）
    List<MessageDTO> getFullMessageTree();
    
    // 更新留言
    MessageDTO updateMessage(Long messageId, MessageDTO messageDTO);
    
    // 删除留言及其所有回复
    void deleteMessage(Long messageId);
} 