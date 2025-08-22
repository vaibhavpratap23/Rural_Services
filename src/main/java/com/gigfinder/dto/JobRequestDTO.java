package com.gigfinder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequestDTO {
    private String title;
    private String description;
    private Long categoryId;
    private Long subCategoryId;
    private BigDecimal budget;
    private String address;
    private String scheduledAt; // We'll use String for now, convert to OffsetDateTime in service
}
