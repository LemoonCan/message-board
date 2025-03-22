package lemoon.messageboard.controller;

import lemoon.messageboard.application.dto.MessageDTO;
import lemoon.messageboard.application.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;

    @PostMapping("/createMessage")
    public ResponseEntity<MessageDTO> createMessage(@RequestBody MessageDTO messageDTO) {
        MessageDTO createdMessage = messageService.createRootMessage(messageDTO);
        return new ResponseEntity<>(createdMessage, HttpStatus.CREATED);
    }
    
    @PostMapping("/{parentId}/reply")
    public ResponseEntity<MessageDTO> replyToMessage(
            @PathVariable("parentId") Long parentId,
            @RequestBody MessageDTO messageDTO) {
        MessageDTO reply = messageService.replyToMessage(parentId, messageDTO);
        return new ResponseEntity<>(reply, HttpStatus.CREATED);
    }
    
    @GetMapping("/root")
    public ResponseEntity<List<MessageDTO>> getRootMessages() {
        List<MessageDTO> rootMessages = messageService.getAllRootMessages();
        return new ResponseEntity<>(rootMessages, HttpStatus.OK);
    }
    
    @GetMapping("/{messageId}/children")
    public ResponseEntity<List<MessageDTO>> getChildrenMessages(@PathVariable Long messageId) {
        List<MessageDTO> children = messageService.getChildrenMessages(messageId);
        return new ResponseEntity<>(children, HttpStatus.OK);
    }
    
    @GetMapping("/tree")
    public ResponseEntity<List<MessageDTO>> getMessageTree(
            @RequestParam(defaultValue = "3") Integer maxDepth) {
        List<MessageDTO> messageTree = messageService.getMessageTree(maxDepth);
        return new ResponseEntity<>(messageTree, HttpStatus.OK);
    }
    
    @GetMapping("/{messageId}/tree")
    public ResponseEntity<MessageDTO> getMessageTreeById(
            @PathVariable Long messageId,
            @RequestParam(defaultValue = "3") Integer maxDepth) {
        MessageDTO messageTree = messageService.getMessageTreeById(messageId, maxDepth);
        return new ResponseEntity<>(messageTree, HttpStatus.OK);
    }
    
    @GetMapping("/full-tree")
    public ResponseEntity<List<MessageDTO>> getFullMessageTree() {
        List<MessageDTO> fullMessageTree = messageService.getFullMessageTree();
        return new ResponseEntity<>(fullMessageTree, HttpStatus.OK);
    }
    
    @PutMapping("/{messageId}")
    public ResponseEntity<MessageDTO> updateMessage(
            @PathVariable Long messageId,
            @RequestBody MessageDTO messageDTO) {
        MessageDTO updatedMessage = messageService.updateMessage(messageId, messageDTO);
        return new ResponseEntity<>(updatedMessage, HttpStatus.OK);
    }
    
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 