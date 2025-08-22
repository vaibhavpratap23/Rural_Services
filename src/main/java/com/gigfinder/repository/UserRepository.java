package com.gigfinder.repository;

import com.gigfinder.model.User;
import com.gigfinder.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") String role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startTime")
    Long countActiveUsersToday(@Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startTime")
    Long countNewRegistrationsToday(@Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startTime AND :endTime")
    Long countByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
