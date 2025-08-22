package com.gigfinder.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServicePackageDTO {
    private Long id;
    
    @NotEmpty(message = "Package name is required")
    private String name;
    
    private String description;
    private Long categoryId;
    private String categoryName;
    
    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;
    
    private Integer estimatedHours;
    private String packageType;
    private Boolean isActive;
    private String features;
    private LocalDateTime createdAt;
}
