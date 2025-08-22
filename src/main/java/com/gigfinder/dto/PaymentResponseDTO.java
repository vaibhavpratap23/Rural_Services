package com.gigfinder.dto;

import com.gigfinder.model.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
    private Long id;
    private Long jobId;
    private String clientName;
    private String workerName;
    private BigDecimal amount;
    private PaymentStatus status;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paymentDate;
    private String failureReason;
    private LocalDateTime createdAt;
}
