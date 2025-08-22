package com.gigfinder.service;

import com.gigfinder.dto.PlatformStatsDTO;
import com.gigfinder.model.*;
import com.gigfinder.model.enums.VerificationStatus;
import com.gigfinder.model.enums.ReportStatus;
import com.gigfinder.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    private final WorkerProfileRepository workerRepository;
    private final JobRepository jobRepository;
    private final PaymentRepository paymentRepository;
    private final RatingRepository ratingRepository;
    private final ReportRepository reportRepository;
    private final PaymentRefundRepository refundRepository;
    
    public PlatformStatsDTO getPlatformOverview() {
        PlatformStatsDTO stats = new PlatformStatsDTO();
        
        // User statistics
        stats.setTotalUsers(userRepository.count());
        stats.setTotalWorkers(workerRepository.count());
        stats.setTotalClients(userRepository.countByRole("CLIENT"));
        
        // Job statistics
        stats.setTotalJobs(jobRepository.count());
        stats.setCompletedJobs(jobRepository.countByStatus("COMPLETED"));
        stats.setPendingJobs(jobRepository.countByStatus("OPEN"));
        
        // Revenue statistics
        BigDecimal totalRevenue = paymentRepository.sumCompletedPayments();
        stats.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        
        // Rating statistics
        BigDecimal avgRating = ratingRepository.getAverageRating();
        stats.setAverageRating(avgRating != null ? avgRating : BigDecimal.ZERO);
        stats.setTotalRatings(ratingRepository.count());
        
        // Today's statistics
        LocalDateTime today = LocalDate.now().atStartOfDay();
        stats.setActiveUsersToday(userRepository.countActiveUsersToday(today));
        stats.setNewRegistrationsToday(userRepository.countNewRegistrationsToday(today));
        stats.setJobsPostedToday(jobRepository.countJobsPostedToday(today));
        stats.setJobsCompletedToday(jobRepository.countJobsCompletedToday(today));
        
        // Most popular category
        stats.setMostPopularCategory(jobRepository.findMostPopularCategory());
        
        return stats;
    }
    
    public Map<String, Object> getDailyStats(LocalDate date) {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        
        stats.put("date", date);
        stats.put("newUsers", userRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        stats.put("newJobs", jobRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        stats.put("completedJobs", jobRepository.countCompletedJobsBetween(startOfDay, endOfDay));
        stats.put("totalRevenue", paymentRepository.sumRevenueBetween(startOfDay, endOfDay));
        stats.put("newRatings", ratingRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        
        return stats;
    }
    
    public List<WorkerProfile> getPendingWorkers() {
        return workerRepository.findByVerificationStatus(VerificationStatus.PENDING);
    }
    
    public WorkerProfile approveWorker(Long workerId) {
        WorkerProfile worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        worker.setVerificationStatus(VerificationStatus.APPROVED);
        return workerRepository.save(worker);
    }
    
    public WorkerProfile rejectWorker(Long workerId, String reason) {
        WorkerProfile worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        worker.setVerificationStatus(VerificationStatus.REJECTED);
        // You could store the rejection reason in a separate field
        return workerRepository.save(worker);
    }
    
    public List<Report> getPendingReports() {
        return reportRepository.findByStatus(ReportStatus.OPEN);
    }
    
    public Report resolveReport(Long reportId, String action) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setStatus(ReportStatus.RESOLVED);
        // You could store the resolution action in a separate field
        return reportRepository.save(report);
    }
    
    public List<PaymentRefund> getPendingRefunds() {
        return refundRepository.findByRefundStatus("PENDING");
    }
    
    public PaymentRefund approveRefund(Long refundId) {
        PaymentRefund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("Refund not found"));
        refund.setRefundStatus("APPROVED");
        refund.setProcessedAt(LocalDateTime.now());
        return refundRepository.save(refund);
    }
    
    public PaymentRefund rejectRefund(Long refundId, String reason) {
        PaymentRefund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("Refund not found"));
        refund.setRefundStatus("REJECTED");
        refund.setAdminNotes(reason);
        refund.setProcessedAt(LocalDateTime.now());
        return refundRepository.save(refund);
    }
}
