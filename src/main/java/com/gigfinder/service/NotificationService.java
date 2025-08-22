package com.gigfinder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public void sendJobNotification(Long userId, String type, Map<String, Object> data) {
        messagingTemplate.convertAndSend("/topic/user/" + userId, Map.of(
            "type", type,
            "data", data,
            "timestamp", System.currentTimeMillis()
        ));
    }
    
    public void sendJobAccepted(Long clientId, Long jobId, String workerName) {
        sendJobNotification(clientId, "JOB_ACCEPTED", Map.of(
            "jobId", jobId,
            "workerName", workerName,
            "message", "Your job has been accepted by " + workerName
        ));
    }
    
    public void sendJobStarted(Long clientId, Long jobId) {
        sendJobNotification(clientId, "JOB_STARTED", Map.of(
            "jobId", jobId,
            "message", "Work has started on your job"
        ));
    }
    
    public void sendJobCompleted(Long clientId, Long jobId) {
        sendJobNotification(clientId, "JOB_COMPLETED", Map.of(
            "jobId", jobId,
            "message", "Your job has been completed"
        ));
    }
    
    public void sendNewJobAvailable(Long workerId, Long jobId, String jobTitle) {
        sendJobNotification(workerId, "NEW_JOB", Map.of(
            "jobId", jobId,
            "jobTitle", jobTitle,
            "message", "New job available: " + jobTitle
        ));
    }
}
