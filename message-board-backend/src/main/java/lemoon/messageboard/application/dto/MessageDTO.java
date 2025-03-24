package lemoon.messageboard.application.dto;

import jakarta.validation.constraints.Size;
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
    @Size(min = 3, max = 200, message = "内容需在3到200字符之间")
    private String content;
    private String customerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MessageDTO> children = new ArrayList<>();
} 