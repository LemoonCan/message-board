package lemoon.messageboard.controller;

import jakarta.validation.Valid;
import lemoon.messageboard.application.dto.MessageDTO;
import lemoon.messageboard.application.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@RequestBody @Valid MessageDTO messageDTO) {
        MessageDTO createdMessage = messageService.createRootMessage(messageDTO);
        return new ResponseEntity<>(createdMessage, HttpStatus.CREATED);
    }
    
    @PostMapping("/{parentId}/reply")
    public ResponseEntity<MessageDTO> replyToMessage(
            @PathVariable("parentId") Long parentId,
            @RequestBody @Valid MessageDTO messageDTO) {
        MessageDTO reply = messageService.replyToMessage(parentId, messageDTO);
        return new ResponseEntity<>(reply, HttpStatus.CREATED);
    }
    
    @GetMapping("/full-tree")
    public ResponseEntity<List<MessageDTO>> getFullMessageTree() {
        List<MessageDTO> fullMessageTree = messageService.getFullMessageTree();
        return new ResponseEntity<>(fullMessageTree, HttpStatus.OK);
    }
} 