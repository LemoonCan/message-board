package lemoon.messageboard.application.service.impl;

import lemoon.messageboard.application.dto.MessageDTO;
import lemoon.messageboard.model.Customer;
import lemoon.messageboard.model.Message;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author lemoon
 * @since 2025/3/23
 */
public class MessageConverter {
    public static MessageDTO toDTO(Message message) {
        if (message == null) {
            return null;
        }
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setCustomerId(message.getCustomer().getId());
        dto.setCustomerName(message.getCustomer().getName());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        return dto;
    }

    public static Message toEntity(MessageDTO messageDTO) {
        Message message = new Message();

        Customer customer = new Customer();
        customer.setId(messageDTO.getCustomerId());
        message.setCustomer(customer);

        message.setContent(messageDTO.getContent());
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }
}
