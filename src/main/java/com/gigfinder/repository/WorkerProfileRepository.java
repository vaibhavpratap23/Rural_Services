package com.gigfinder.repository;

import com.gigfinder.model.User;
import com.gigfinder.model.WorkerProfile;
import com.gigfinder.model.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, Long> {
    Optional<WorkerProfile> findByUser(User user);
    
    @Query("SELECT w FROM WorkerProfile w WHERE w.isAvailable = true " +
           "AND (:category IS NULL OR w.skills LIKE CONCAT('%', :category, '%')) " +
           "AND (:latitude IS NULL OR :longitude IS NULL OR :radiusKm IS NULL OR " +
           "(6371 * ACOS(COS(RADIANS(:latitude)) * COS(RADIANS(w.locationLat)) * " +
           "COS(RADIANS(w.locationLng) - RADIANS(:longitude)) + " +
           "SIN(RADIANS(:latitude)) * SIN(RADIANS(w.locationLat)))) <= :radiusKm)")
    List<WorkerProfile> findAvailableWorkers(
        @Param("category") String category,
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radiusKm") Double radiusKm);
    List<WorkerProfile> findByVerificationStatus(VerificationStatus status);
    List<WorkerProfile> findByVerificationStatusIn(List<VerificationStatus> statuses);
    List<WorkerProfile> findBySkillsContaining(String skill);
    
    List<WorkerProfile> findTop10ByOrderByRatingAvgDesc();
    
    @Query("SELECT w FROM WorkerProfile w WHERE w.locationLat BETWEEN :minLat AND :maxLat " +
           "AND w.locationLng BETWEEN :minLng AND :maxLng")
    List<WorkerProfile> findByLocationWithinBounds(
        @Param("minLat") java.math.BigDecimal minLat,
        @Param("maxLat") java.math.BigDecimal maxLat,
        @Param("minLng") java.math.BigDecimal minLng,
        @Param("maxLng") java.math.BigDecimal maxLng);
}
