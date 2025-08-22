package com.gigfinder.repository;

import com.gigfinder.model.Payment;
import com.gigfinder.model.User;
import com.gigfinder.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByClient(User client);
    List<Payment> findByWorker(User worker);
    List<Payment> findByStatus(PaymentStatus status);
    Optional<Payment> findByJobId(Long jobId);
    List<Payment> findByClientAndStatus(User client, PaymentStatus status);
    List<Payment> findByWorkerAndStatus(User worker, PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal sumCompletedPayments();
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt BETWEEN :startTime AND :endTime")
    BigDecimal sumRevenueBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
