package com.gigfinder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.gigfinder.model.Job;
import com.gigfinder.model.User;
import com.gigfinder.model.enums.JobStatus;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    List<Job> findByStatus(JobStatus status);
    List<Job> findByCategoryId(Long categoryId);
    List<Job> findByStatusOrderByCreatedAtDesc(JobStatus status);
    
    @Query("SELECT j FROM Job j JOIN j.client c WHERE c.locationLat BETWEEN :minLat AND :maxLat " +
           "AND c.locationLng BETWEEN :minLon AND :maxLon " +
           "AND j.status = :status")
    List<Job> findByLocationWithinBounds(
        @Param("minLat") double minLat, 
        @Param("maxLat") double maxLat,
        @Param("minLon") double minLon, 
        @Param("maxLon") double maxLon,
        @Param("status") JobStatus status);
    List<Job> findByClientUser(User user);
    
    @Query("SELECT COUNT(j) FROM Job j WHERE j.status = :status")
    Long countByStatus(@Param("status") String status);
    
    @Query("SELECT COUNT(j) FROM Job j WHERE j.createdAt >= :startTime")
    Long countJobsPostedToday(@Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT COUNT(j) FROM Job j WHERE j.status = 'COMPLETED' AND j.createdAt >= :startTime")
    Long countJobsCompletedToday(@Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT COUNT(j) FROM Job j WHERE j.createdAt BETWEEN :startTime AND :endTime")
    Long countByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(j) FROM Job j WHERE j.status = 'COMPLETED' AND j.createdAt BETWEEN :startTime AND :endTime")
    Long countCompletedJobsBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT c.name FROM Job j JOIN j.category c GROUP BY c.name ORDER BY COUNT(j) DESC LIMIT 1")
    String findMostPopularCategory();
}
