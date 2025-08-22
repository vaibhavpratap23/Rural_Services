package com.gigfinder.service;

import com.gigfinder.dto.MessageDTO;
import com.gigfinder.model.Job;
import com.gigfinder.model.Message;
import com.gigfinder.model.User;
import com.gigfinder.repository.JobRepository;
import com.gigfinder.repository.MessageRepository;
import com.gigfinder.repository.UserRepository;
import com.gigfinder.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    
    public Message sendMessage(MessageDTO messageDTO) {
        String username = SecurityUtil.getCurrentUsername();
        User sender = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        User receiver = userRepository.findById(messageDTO.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        Job job = null;
        if (messageDTO.getJobId() != null) {
            job = jobRepository.findById(messageDTO.getJobId())
                    .orElseThrow(() -> new RuntimeException("Job not found"));
        }
        
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .job(job)
                .content(messageDTO.getContent())
                .messageType(messageDTO.getMessageType() != null ? messageDTO.getMessageType() : "TEXT")
                .isRead(false)
                .build();
        
        return messageRepository.save(message);
    }
    
    public List<Message> getConversation(Long userId) {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        User otherUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return messageRepository.findConversationBetweenUsers(currentUser, otherUser);
    }
    
    public List<Message> getReceivedMessages() {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return messageRepository.findByReceiverOrderByCreatedAtDesc(currentUser);
    }
    
    public List<Message> getSentMessages() {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return messageRepository.findBySenderOrderByCreatedAtDesc(currentUser);
    }
    
    public Message markAsRead(Long messageId) {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        // Verify the current user is the receiver
        if (!message.getReceiver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to mark this message as read");
        }
        
        message.setIsRead(true);
        return messageRepository.save(message);
    }
    
    public Long getUnreadCount() {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return messageRepository.countUnreadMessagesByReceiver(currentUser);
    }
}
