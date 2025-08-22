package com.gigfinder.dto;

import com.gigfinder.model.enums.ScheduleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobScheduleDTO {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long workerId;
    private String workerName;
    private String clientName;
    
    @NotNull(message = "Scheduled date is required")
    private LocalDateTime scheduledDate;
    
    private Integer estimatedDurationHours;
    private ScheduleStatus status;
    private String notes;
    private LocalDateTime createdAt;
}
