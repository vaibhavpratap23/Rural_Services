package com.gigfinder.repository;

import com.gigfinder.model.Rating;
import com.gigfinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByReviewee(User reviewee);
    
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.reviewee = :reviewee")
    BigDecimal getAverageRatingByReviewee(@Param("reviewee") User reviewee);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.reviewee = :reviewee")
    Long getRatingCountByReviewee(@Param("reviewee") User reviewee);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.job = :jobId AND r.reviewer = :reviewer")
    boolean existsByJobAndReviewer(@Param("jobId") Long jobId, @Param("reviewer") User reviewer);
    
    @Query("SELECT AVG(r.score) FROM Rating r")
    BigDecimal getAverageRating();
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.createdAt BETWEEN :startTime AND :endTime")
    Long countByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}






