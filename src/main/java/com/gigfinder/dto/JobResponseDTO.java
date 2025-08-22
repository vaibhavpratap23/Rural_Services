package com.gigfinder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String categoryName;
    private String subCategoryName;
    private BigDecimal budget;
    private String address;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime scheduledAt;
}
