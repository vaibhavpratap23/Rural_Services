package com.gigfinder.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlatformStatsDTO {
    private Long totalUsers;
    private Long totalWorkers;
    private Long totalClients;
    private Long totalJobs;
    private Long completedJobs;
    private Long pendingJobs;
    private BigDecimal totalRevenue;
    private BigDecimal averageRating;
    private Long totalRatings;
    private String mostPopularCategory;
    private Long activeUsersToday;
    private Long newRegistrationsToday;
    private Long jobsPostedToday;
    private Long jobsCompletedToday;
}
