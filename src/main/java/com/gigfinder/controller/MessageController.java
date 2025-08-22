package com.gigfinder.controller;

import com.gigfinder.dto.MessageDTO;
import com.gigfinder.model.Message;
import com.gigfinder.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    
    @PostMapping
    public ResponseEntity<?> sendMessage(@Valid @RequestBody MessageDTO messageDTO) {
        try {
            Message message = messageService.sendMessage(messageDTO);
            return ResponseEntity.ok(convertToDTO(message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending message: " + e.getMessage());
        }
    }
    
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<?> getConversation(@PathVariable Long userId) {
        try {
            List<Message> messages = messageService.getConversation(userId);
            return ResponseEntity.ok(messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching conversation: " + e.getMessage());
        }
    }
    
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedMessages() {
        try {
            List<Message> messages = messageService.getReceivedMessages();
            return ResponseEntity.ok(messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching received messages: " + e.getMessage());
        }
    }
    
    @GetMapping("/sent")
    public ResponseEntity<?> getSentMessages() {
        try {
            List<Message> messages = messageService.getSentMessages();
            return ResponseEntity.ok(messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching sent messages: " + e.getMessage());
        }
    }
    
    @PutMapping("/{messageId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long messageId) {
        try {
            Message message = messageService.markAsRead(messageId);
            return ResponseEntity.ok(convertToDTO(message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error marking message as read: " + e.getMessage());
        }
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadCount() {
        try {
            Long count = messageService.getUnreadCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching unread count: " + e.getMessage());
        }
    }
    
    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setJobId(message.getJob() != null ? message.getJob().getId() : null);
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setIsRead(message.getIsRead());
        dto.setSenderName(message.getSender().getName());
        dto.setReceiverName(message.getReceiver().getName());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
}
