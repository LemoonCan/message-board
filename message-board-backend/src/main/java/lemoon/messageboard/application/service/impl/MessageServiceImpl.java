package lemoon.messageboard.application.service.impl;

import lemoon.messageboard.application.dto.MessageDTO;
import lemoon.messageboard.application.service.MessageService;
import lemoon.messageboard.model.Customer;
import lemoon.messageboard.model.Message;
import lemoon.messageboard.model.MessageClosure;
import lemoon.messageboard.repository.MessageClosureRepository;
import lemoon.messageboard.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageClosureRepository messageClosureRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, MessageClosureRepository messageClosureRepository) {
        this.messageRepository = messageRepository;
        this.messageClosureRepository = messageClosureRepository;
    }

    // 将消息实体转换为DTO
    private MessageDTO convertToDTO(Message message) {
        if (message == null) {
            return null;
        }
        
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setCustomerId(message.getCustomer().getId());
        dto.setCustomerName(message.getCustomer().getName());
        dto.setCreatedTime(message.getCreatedTime());
        dto.setUpdatedTime(message.getUpdatedTime());
        return dto;
    }

    // 将DTO转换为消息实体
    private Message convertToEntity(MessageDTO dto, Customer customer) {
        Message message = new Message();
        message.setContent(dto.getContent());
        message.setCustomer(customer);
        message.setCreatedTime(LocalDateTime.now());
        return message;
    }

    @Override
    @Transactional
    public MessageDTO createRootMessage(MessageDTO messageDTO) {
        // 假设已经有Customer实体，这里简化处理
        Customer customer = new Customer();
        customer.setId(messageDTO.getCustomerId());
        
        Message message = convertToEntity(messageDTO, customer);
        message = messageRepository.save(message);
        
        // 为新消息创建自引用的闭包表记录（深度为0）
        MessageClosure closure = new MessageClosure();
        closure.setAncestor(message);
        closure.setDescendant(message);
        closure.setDepth(0);
        messageClosureRepository.save(closure);
        
        return convertToDTO(message);
    }

    @Override
    @Transactional
    public MessageDTO replyToMessage(Long parentId, MessageDTO messageDTO) {
        Message parentMessage = messageRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("父留言不存在"));
        
        // 假设已经有Customer实体，这里简化处理
        Customer customer = new Customer();
        customer.setId(messageDTO.getCustomerId());
        
        Message message = convertToEntity(messageDTO, customer);
        message = messageRepository.save(message);
        
        // 1. 创建自引用的闭包表记录（深度为0）
        MessageClosure selfClosure = new MessageClosure();
        selfClosure.setAncestor(message);
        selfClosure.setDescendant(message);
        selfClosure.setDepth(0);
        messageClosureRepository.save(selfClosure);
        
        // 2. 创建与父留言的直接关系（深度为1）
        MessageClosure parentClosure = new MessageClosure();
        parentClosure.setAncestor(parentMessage);
        parentClosure.setDescendant(message);
        parentClosure.setDepth(1);
        messageClosureRepository.save(parentClosure);
        
        // 3. 复制父留言的所有祖先关系
        List<MessageClosure> parentClosures = messageClosureRepository.findAll().stream()
                .filter(mc -> mc.getDescendant().getId().equals(parentId) && mc.getDepth() > 0)
                .collect(Collectors.toList());
        
        List<MessageClosure> newClosures = new ArrayList<>();
        for (MessageClosure pc : parentClosures) {
            MessageClosure newClosure = new MessageClosure();
            newClosure.setAncestor(pc.getAncestor());
            newClosure.setDescendant(message);
            newClosure.setDepth(pc.getDepth() + 1);
            newClosures.add(newClosure);
        }
        
        if (!newClosures.isEmpty()) {
            messageClosureRepository.saveAll(newClosures);
        }
        
        return convertToDTO(message);
    }

    @Override
    public List<MessageDTO> getAllRootMessages() {
        return messageClosureRepository.findRootMessages().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getChildrenMessages(Long messageId) {
        return messageClosureRepository.findChildren(messageId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getMessageTree(Integer maxDepth) {
        List<MessageDTO> rootMessages = getAllRootMessages();
        
        // 为每个根留言构建其子树
        for (MessageDTO rootMessage : rootMessages) {
            buildMessageTree(rootMessage, maxDepth, 1);
        }
        
        return rootMessages;
    }

    @Override
    public MessageDTO getMessageTreeById(Long messageId, Integer maxDepth) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("留言不存在"));
        
        MessageDTO rootDTO = convertToDTO(message);
        buildMessageTree(rootDTO, maxDepth, 1);
        
        return rootDTO;
    }
    
    @Override
    public List<MessageDTO> getFullMessageTree() {
        List<MessageDTO> rootMessages = getAllRootMessages();
        
        // 为每个根留言构建其完整子树（无深度限制）
        for (MessageDTO rootMessage : rootMessages) {
            buildFullMessageTree(rootMessage);
        }
        
        return rootMessages;
    }
    
    // 递归构建完整消息树（无深度限制）
    private void buildFullMessageTree(MessageDTO parentDTO) {
        List<MessageDTO> children = getChildrenMessages(parentDTO.getId());
        parentDTO.setChildren(children);
        
        for (MessageDTO child : children) {
            buildFullMessageTree(child);
        }
    }
    
    // 递归构建消息树
    private void buildMessageTree(MessageDTO parentDTO, Integer maxDepth, int currentDepth) {
        if (currentDepth > maxDepth) {
            return;
        }
        
        List<MessageDTO> children = getChildrenMessages(parentDTO.getId());
        parentDTO.setChildren(children);
        
        for (MessageDTO child : children) {
            buildMessageTree(child, maxDepth, currentDepth + 1);
        }
    }

    @Override
    @Transactional
    public MessageDTO updateMessage(Long messageId, MessageDTO messageDTO) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("留言不存在"));
        
        message.setContent(messageDTO.getContent());
        message.setUpdatedTime(LocalDateTime.now());
        message = messageRepository.save(message);
        
        return convertToDTO(message);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId) {
        // 找到所有需要删除的后代留言ID
        List<Message> descendants = messageClosureRepository.findDescendants(messageId);
        List<Long> descendantIds = descendants.stream()
                .map(Message::getId)
                .collect(Collectors.toList());
        
        // 删除闭包表中相关的所有记录
        for (Long id : descendantIds) {
            messageClosureRepository.deleteByMessageId(id);
        }
        
        // 删除消息实体
        messageRepository.deleteById(messageId);
        for (Long id : descendantIds) {
            if (!id.equals(messageId)) {
                messageRepository.deleteById(id);
            }
        }
    }
} 