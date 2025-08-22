package com.gigfinder.dto;

import jakarta.validation.constraints.*;
import com.gigfinder.model.enums.ReportStatus;

public class ReportDTO {
    
    @NotNull(message = "Reported user ID is required")
    private Long reportedUserId;
    
    @NotNull(message = "Report reason is required")
    @NotEmpty(message = "Report reason cannot be empty")
    @Size(max = 1000, message = "Report reason must be less than 1000 characters")
    private String reason;
    
    @Size(max = 500, message = "Additional details must be less than 500 characters")
    private String additionalDetails;
    
    // Constructors, getters, and setters
    public ReportDTO() {}
    
    public ReportDTO(Long reportedUserId, String reason, String additionalDetails) {
        this.reportedUserId = reportedUserId;
        this.reason = reason;
        this.additionalDetails = additionalDetails;
    }
    
    public Long getReportedUserId() { return reportedUserId; }
    public void setReportedUserId(Long reportedUserId) { this.reportedUserId = reportedUserId; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getAdditionalDetails() { return additionalDetails; }
    public void setAdditionalDetails(String additionalDetails) { this.additionalDetails = additionalDetails; }
}







