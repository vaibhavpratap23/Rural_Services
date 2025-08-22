package com.gigfinder.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    
    @NotNull(message = "Receiver ID is required")
    private Long receiverId;
    
    private Long jobId;
    
    @NotEmpty(message = "Message content is required")
    private String content;
    
    private String messageType;
    private Boolean isRead;
    private String senderName;
    private String receiverName;
    private LocalDateTime createdAt;
}
