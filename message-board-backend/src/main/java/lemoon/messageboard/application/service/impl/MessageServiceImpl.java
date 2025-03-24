package lemoon.messageboard.application.service.impl;

import lemoon.messageboard.application.dto.MessageDTO;
import lemoon.messageboard.application.service.MessageService;
import lemoon.messageboard.model.Message;
import lemoon.messageboard.repository.CustomerRepository;
import lemoon.messageboard.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lemoon
 * @since 2025/3/24
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;

    @Override
    public MessageDTO createRootMessage(MessageDTO messageDTO) {
        if (customerRepository.existsById(messageDTO.getCustomerId())) {
            throw new RuntimeException("用户不存在");
        }

        Message message = messageRepository.save(MessagesConverter.toEntity(messageDTO));
        return MessagesConverter.toDTO(message);
    }

    @Override
    public MessageDTO replyToMessage(Long parentId, MessageDTO messageDTO) {
        if (customerRepository.existsById(messageDTO.getCustomerId())) {
            throw new RuntimeException("用户不存在");
        }
        if (!messageRepository.existsById(parentId)) {
            throw new RuntimeException("父消息不存在");
        }

        Message message = MessagesConverter.toEntity(messageDTO);
        Message parentMessage = new Message();
        parentMessage.setId(parentId);
        message.setParent(parentMessage);
        message = messageRepository.save(message);
        return MessagesConverter.toDTO(message);
    }

    @Override
    public List<MessageDTO> getFullMessageTree() {
        Map<Long, List<MessageDTO>> parentMap = new HashMap<>();
        List<MessageDTO> roots = new ArrayList<>();

        List<Message> messages = messageRepository.findAllByIdAtDesc();
        messages.forEach(message -> {
            if (message.getParent() == null) {
                roots.add(MessagesConverter.toDTO(message));
            } else {
                List<MessageDTO> children = parentMap.getOrDefault(message.getParent().getId(), new ArrayList<>());
                children.add(MessagesConverter.toDTO(message));
                parentMap.put(message.getParent().getId(), children);
            }
        });

        roots.forEach(root -> buildChildren(parentMap, root));

        return roots;
    }

    private void buildChildren(Map<Long, List<MessageDTO>> parentMap, MessageDTO cur) {
        if (parentMap.containsKey(cur.getId())) {
            cur.setChildren(parentMap.get(cur.getId()));
            cur.getChildren().forEach(child -> buildChildren(parentMap, child));
        }
    }
}
