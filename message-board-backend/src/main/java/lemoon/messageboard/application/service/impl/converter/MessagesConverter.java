package lemoon.messageboard.application.service.impl.converter;

import lemoon.messageboard.application.dto.MessageDTO;
import lemoon.messageboard.model.Customer;
import lemoon.messageboard.model.Message;

import java.time.LocalDateTime;

/**
 * @author lemoon
 * @since 2025/3/24
 */
public class MessagesConverter {
    public static MessageDTO toDTO(Message message) {
        if (message == null) {
            return null;
        }
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setCustomerName(message.getCustomer().getName());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        return dto;
    }

    public static Message toEntity(MessageDTO messageDTO,Customer customer) {
        Message message = new Message();
        message.setCustomer(customer);
        message.setContent(messageDTO.getContent());
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }
}
