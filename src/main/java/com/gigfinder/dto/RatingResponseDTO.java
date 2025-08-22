package com.gigfinder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDTO {
    private Long id;
    private String jobTitle;
    private String reviewerName;
    private String revieweeName;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
}






