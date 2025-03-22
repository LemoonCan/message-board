package lemoon.messageboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private String content;
    private Long customerId;
    private String customerName;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private List<MessageDTO> children = new ArrayList<>();
} 