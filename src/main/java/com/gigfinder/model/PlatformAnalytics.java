package com.gigfinder.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "platform_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformAnalytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date", nullable = false, unique = true)
    private LocalDate date;
    
    @Column(name = "total_jobs_posted")
    private Integer totalJobsPosted;
    
    @Column(name = "total_jobs_completed")
    private Integer totalJobsCompleted;
    
    @Column(name = "total_revenue", precision = 12, scale = 2)
    private BigDecimal totalRevenue;
    
    @Column(name = "active_users")
    private Integer activeUsers;
    
    @Column(name = "new_registrations")
    private Integer newRegistrations;
    
    @Column(name = "total_ratings")
    private Integer totalRatings;
    
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;
    
    @Column(name = "most_popular_category")
    private String mostPopularCategory;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;
}
