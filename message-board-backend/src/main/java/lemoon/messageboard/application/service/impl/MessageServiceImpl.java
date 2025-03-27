package lemoon.messageboard.application.service.impl;

import lemoon.messageboard.application.dto.MessageDTO;
import lemoon.messageboard.application.service.MessageService;
import lemoon.messageboard.application.service.impl.converter.MessagesConverter;
import lemoon.messageboard.model.Customer;
import lemoon.messageboard.model.Message;
import lemoon.messageboard.model.MessageInfo;
import lemoon.messageboard.repository.CustomerRepository;
import lemoon.messageboard.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author lemoon
 * @since 2025/3/24
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    @Override
    public MessageDTO createRootMessage(MessageDTO messageDTO) {
        Optional<Customer> customerOptional = customerRepository.findByName(messageDTO.getCustomerName());
        if (customerOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        Message message = messageRepository.save(MessagesConverter.toEntity(messageDTO, customerOptional.get()));
        return MessagesConverter.toDTO(message);
    }

    @Transactional
    @Override
    public MessageDTO replyToMessage(Long parentId, MessageDTO messageDTO) {
        Optional<Customer> customerOptional = customerRepository.findByName(messageDTO.getCustomerName());
        if (customerOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        if (!messageRepository.existsById(parentId)) {
            throw new RuntimeException("父消息不存在");
        }

        Message message = MessagesConverter.toEntity(messageDTO, customerOptional.get());
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

        List<MessageInfo> messages = messageRepository.findAllByIdAtDesc();
        messages.forEach(message -> {
            if (message.getParentId() == null) {
                roots.add(MessagesConverter.toDTO(message));
            } else {
                List<MessageDTO> children = parentMap.getOrDefault(message.getParentId(), new ArrayList<>());
                children.add(MessagesConverter.toDTO(message));
                parentMap.put(message.getParentId(), children);
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
